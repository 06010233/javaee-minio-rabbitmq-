package com.example.flight.service;

import com.example.flight.entity.Airline;
import com.example.flight.entity.Airport;
import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightRoute;
import com.example.flight.exception.ResourceNotFoundException;
import com.example.flight.repository.AirlineRepository;
import com.example.flight.repository.AirportRepository;
import com.example.flight.repository.FlightRepository;
import com.example.flight.repository.FlightRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlightRouteService {

    private final FlightRouteRepository flightRouteRepository;
    private final FlightRepository flightRepository;
    private final FlightService flightService;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final FlightSeatAvailabilityService availabilityService; // 新增注入

    public FlightRouteService(FlightRouteRepository flightRouteRepository,
                              FlightRepository flightRepository,
                              FlightService flightService,
                              AirlineRepository airlineRepository,
                              AirportRepository airportRepository,
                              FlightSeatAvailabilityService availabilityService) {
        this.flightRouteRepository = flightRouteRepository;
        this.flightRepository = flightRepository;
        this.flightService = flightService;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.availabilityService = availabilityService;
    }
    
    public List<FlightRoute> findAll() {
        return flightRouteRepository.findAll();
    }

    public Optional<FlightRoute> findById(Integer id) {
        return flightRouteRepository.findById(id);
    }
    
    @Transactional
    public FlightRoute create(FlightRoute routeRequest) {
        Airline airline = airlineRepository.findById(routeRequest.getAirline().getAirlineId())
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found"));
        Airport departureAirport = airportRepository.findById(routeRequest.getDepartureAirport().getAirportCode())
                .orElseThrow(() -> new ResourceNotFoundException("Departure airport not found"));
        Airport arrivalAirport = airportRepository.findById(routeRequest.getArrivalAirport().getAirportCode())
                .orElseThrow(() -> new ResourceNotFoundException("Arrival airport not found"));

        FlightRoute newRoute = new FlightRoute();
        
        newRoute.setFlightNumber(routeRequest.getFlightNumber());
        newRoute.setAircraftModel(routeRequest.getAircraftModel());
        newRoute.setBusinessSeats(routeRequest.getBusinessSeats());
        newRoute.setBusinessSeatPrice(routeRequest.getBusinessSeatPrice());
        newRoute.setEconomySeats(routeRequest.getEconomySeats());
        newRoute.setEconomySeatPrice(routeRequest.getEconomySeatPrice());
        newRoute.setActive(routeRequest.isActive());
        
        newRoute.setAirline(airline);
        newRoute.setDepartureAirport(departureAirport);
        newRoute.setArrivalAirport(arrivalAirport);
        
        updateAndCalculateDuration(newRoute, routeRequest.getDepartureTime(), routeRequest.getArrivalTime());
        
        return flightRouteRepository.save(newRoute);
    }
    
    @Transactional
    public FlightRoute update(Integer routeId, FlightRoute updatedRouteData) {
        FlightRoute existingRoute = flightRouteRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("FlightRoute not found with id " + routeId));

        boolean wasActive = existingRoute.isActive();
        boolean isActive = updatedRouteData.isActive();

        // 1. 更新航线模板自身的基础属性
        existingRoute.setAircraftModel(updatedRouteData.getAircraftModel());
        existingRoute.setBusinessSeats(updatedRouteData.getBusinessSeats());
        existingRoute.setBusinessSeatPrice(updatedRouteData.getBusinessSeatPrice());
        existingRoute.setEconomySeats(updatedRouteData.getEconomySeats());
        existingRoute.setEconomySeatPrice(updatedRouteData.getEconomySeatPrice());
        existingRoute.setActive(isActive);
        updateAndCalculateDuration(existingRoute, updatedRouteData.getDepartureTime(), updatedRouteData.getArrivalTime());
        
        FlightRoute savedRoute = flightRouteRepository.save(existingRoute);

        // 2. 查找所有未来的航班
        String fullFlightNumber = savedRoute.getAirline().getAirlineCode() + savedRoute.getFlightNumber();
        List<Flight> futureFlights = flightRepository.findByFlightNumberAndDepartureAirportAndArrivalAirportAndDepartureTimeAfter(
                fullFlightNumber,
                savedRoute.getDepartureAirport(),
                savedRoute.getArrivalAirport(),
                LocalDateTime.now()
        );

        // 3. 处理未来航班的状态同步
        if (wasActive && !isActive) {
            // 情况一：航线从“启用”变为“停用”，取消所有未来计划中的航班
            for (Flight flight : futureFlights) {
                if (flight.getStatus() == Flight.FlightStatus.PLANNED) {
                    flight.setStatus(Flight.FlightStatus.CANCELLED);
                    flightService.saveFlight(flight);
                }
            }
        } else if (!wasActive && isActive) {
            // ★★★ 核心修复：航线从“停用”变为“启用”，复活未来已取消的航班 ★★★
            for (Flight flight : futureFlights) {
                if (flight.getStatus() == Flight.FlightStatus.CANCELLED) {
                    flight.setStatus(Flight.FlightStatus.PLANNED);
                    // 同步最新的信息
                    syncFlightWithRoute(flight, savedRoute);
                    flightService.saveFlight(flight);
                }
            }
        } else {
            // 情况三：状态未变，仅更新信息（只更新未起飞且正常的航班）
            for (Flight flight : futureFlights) {
                if (flight.getStatus() == Flight.FlightStatus.PLANNED) {
                    syncFlightWithRoute(flight, savedRoute);
                    flightService.saveFlight(flight);
                }
            }
        }

        return savedRoute;
    }

    public void deleteById(Integer id) {
        flightRouteRepository.deleteById(id);
    }

    public List<Flight> getFutureFlightsForRoute(Integer routeId) {
        FlightRoute route = findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("FlightRoute not found with id " + routeId));
        
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);

        return flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                route.getDepartureAirport(),
                route.getArrivalAirport(),
                start,
                end
        );
    }

    @Transactional
    public List<Flight> scheduleFlightsForRoute(Integer routeId, int daysToSchedule) {
        FlightRoute route = findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("FlightRoute not found with id " + routeId));

        if (!route.isActive()) {
            throw new IllegalStateException("Cannot schedule flights for an inactive route.");
        }

        String fullFlightNumber = route.getAirline().getAirlineCode() + route.getFlightNumber();
        LocalDate startDate = LocalDate.now();
        List<Flight> scheduledFlights = new ArrayList<>();

        for (int i = 0; i < daysToSchedule; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = currentDate.atTime(23, 59, 59);

            // ★★★ 核心修复：检查当天是否已有记录（包括已取消的） ★★★
            // 注意：这需要你在 FlightRepository 中实现了 findExistingFlightOnDay 方法
            Optional<Flight> existingFlightOpt = flightRepository.findExistingFlightOnDay(fullFlightNumber, startOfDay, endOfDay);

            if (existingFlightOpt.isPresent()) {
                Flight existingFlight = existingFlightOpt.get();
                // 如果已存在且是取消状态，则“复活”它
                if (existingFlight.getStatus() == Flight.FlightStatus.CANCELLED) {
                    existingFlight.setStatus(Flight.FlightStatus.PLANNED);
                    existingFlight.setCreatedAt(LocalDateTime.now()); // 可选：更新创建时间以排在前面
                    syncFlightWithRoute(existingFlight, route);
                    // 重新计算当天的具体起降时间
                    LocalDateTime departureDateTime = currentDate.atTime(route.getDepartureTime());
                    LocalDateTime arrivalDateTime = departureDateTime.plusMinutes(route.getFlightDurationMinutes());
                    existingFlight.setDepartureTime(departureDateTime);
                    existingFlight.setArrivalTime(arrivalDateTime);
                    
                    flightService.saveFlight(existingFlight);
                    scheduledFlights.add(existingFlight);
                }
                // 如果已经是 PLANNED 或其他状态，则跳过，避免重复
                continue; 
            }

            // 如果不存在，创建新航班
            Flight newFlight = new Flight();
            
            newFlight.setFlightRoute(route);
            newFlight.setFlightNumber(fullFlightNumber);
            newFlight.setAirline(route.getAirline());
            newFlight.setDepartureAirport(route.getDepartureAirport());
            newFlight.setArrivalAirport(route.getArrivalAirport());
            newFlight.setAircraftModel(route.getAircraftModel());
            
            newFlight.setEconomySeatPrice(route.getEconomySeatPrice());
            newFlight.setBusinessSeatPrice(route.getBusinessSeatPrice());
            
            newFlight.setEconomySeats(route.getEconomySeats());
            newFlight.setBusinessSeats(route.getBusinessSeats());
            newFlight.setTotalSeats(route.getEconomySeats() + route.getBusinessSeats());
            
            LocalDateTime departureDateTime = currentDate.atTime(route.getDepartureTime());
            LocalDateTime arrivalDateTime = departureDateTime.plusMinutes(route.getFlightDurationMinutes());
            
            newFlight.setDepartureTime(departureDateTime);
            newFlight.setArrivalTime(arrivalDateTime);
            
            newFlight.setStatus(Flight.FlightStatus.PLANNED);
            newFlight.setCreatedAt(LocalDateTime.now());

            Flight savedFlight = flightService.saveFlight(newFlight);
            // ★★★ 关键：初始化座位库存 ★★★
            availabilityService.initializeAvailability(savedFlight);
            
            scheduledFlights.add(savedFlight);
        }

        return scheduledFlights;
    }
    
    // 辅助方法：计算时长
    private void updateAndCalculateDuration(FlightRoute route, java.time.LocalTime departureTime, java.time.LocalTime arrivalTime) {
        route.setDepartureTime(departureTime);
        route.setArrivalTime(arrivalTime);
        long duration = Duration.between(departureTime, arrivalTime).toMinutes();
        if (duration < 0) { 
            duration += 24 * 60;
        }
        route.setFlightDurationMinutes((int) duration);
    }

    // 辅助方法：将 Flight 实体的信息与 Route 模板同步
    private void syncFlightWithRoute(Flight flight, FlightRoute route) {
        LocalDateTime newDepartureDateTime = flight.getDepartureTime().toLocalDate().atTime(route.getDepartureTime());
        LocalDateTime newArrivalDateTime = newDepartureDateTime.plusMinutes(route.getFlightDurationMinutes());
        
        flight.setDepartureTime(newDepartureDateTime);
        flight.setArrivalTime(newArrivalDateTime);
        flight.setAircraftModel(route.getAircraftModel());
        flight.setBusinessSeatPrice(route.getBusinessSeatPrice());
        flight.setEconomySeatPrice(route.getEconomySeatPrice());
    }
}