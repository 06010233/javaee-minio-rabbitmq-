// 文件路径: src/main/java/com/example/flight/service/FlightService.java

package com.example.flight.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flight.entity.Airline;
import com.example.flight.entity.Airport;
import com.example.flight.entity.Flight;
import com.example.flight.exception.ResourceNotFoundException;
import com.example.flight.repository.AirlineRepository;
import com.example.flight.repository.AirportRepository;
import com.example.flight.repository.FlightRepository;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final FlightSeatAvailabilityService availabilityService;

    @Autowired
    public FlightService(FlightRepository flightRepository, 
                         AirportRepository airportRepository, 
                         AirlineRepository airlineRepository,
                         FlightSeatAvailabilityService availabilityService) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.availabilityService = availabilityService;
    }

    // 获取所有航班（仅限可见状态）
    public List<Flight> findAll() { 
        List<Flight.FlightStatus> visibleStatuses = Arrays.asList(
            Flight.FlightStatus.PLANNED,
            Flight.FlightStatus.BOARDING,
            Flight.FlightStatus.IN_FLIGHT,
            Flight.FlightStatus.ARRIVED
        );
        return flightRepository.findByStatusIn(visibleStatuses); 
    }
    
    // ★★★ 核心修改：日历数据源 - 只显示【未来】且【未取消】的航班 ★★★
    public List<Flight> getVisibleFlightsForCalendar(LocalDateTime start, LocalDateTime end) {
        // 1. 定义可见状态 (绝对不包含 CANCELLED)
        List<Flight.FlightStatus> visibleStatuses = Arrays.asList(
            Flight.FlightStatus.PLANNED,
            Flight.FlightStatus.BOARDING,
            Flight.FlightStatus.IN_FLIGHT,
            Flight.FlightStatus.ARRIVED
        );

        // 2. 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 3. 逻辑判断：如果请求的开始时间是过去（比如日历视图从12月29日开始），
        // 强制把查询起点设为“现在”。这样“现在”之前的旧航班就不会被查出来。
        if (start.isBefore(now)) {
            start = now;
        }

        // 4. 防止时间倒挂
        if (start.isAfter(end)) {
            return Collections.emptyList();
        }

        // 5. 执行查询
        return flightRepository.findByDepartureTimeBetweenAndStatusIn(start, end, visibleStatuses);
    }

    // 搜索航班 (排除已取消)
    public List<Flight> searchFlights(String departureCode, String arrivalCode, LocalDate date) { 
        Optional<Airport> departureOpt = airportRepository.findById(departureCode); 
        Optional<Airport> arrivalOpt = airportRepository.findById(arrivalCode); 
        
        if (departureOpt.isPresent() && arrivalOpt.isPresent()) { 
            LocalDateTime startOfDay = date.atStartOfDay(); 
            LocalDateTime endOfDay = date.atTime(23, 59, 59); 
            // 这里的 searchAvailableFlights 需要在 Repository 中定义（见之前的步骤）
            return flightRepository.searchAvailableFlights(departureCode, arrivalCode, startOfDay, endOfDay); 
        } 
        return Collections.emptyList(); 
    }

    public Optional<Flight> findById(Integer id) { 
        Optional<Flight> flightOpt = flightRepository.findByIdWithDetails(id); 
        return flightOpt.map(flight -> { 
            availabilityService.findByFlightId(flight.getFlightId()).ifPresent(availability -> { 
                flight.setAvailableBusinessSeats(availability.getAvailableBusinessSeats()); 
                flight.setAvailableEconomySeats(availability.getAvailableEconomySeats()); 
            }); 
            if (flight.getAvailableBusinessSeats() == null) { flight.setAvailableBusinessSeats(0); } 
            if (flight.getAvailableEconomySeats() == null) { flight.setAvailableEconomySeats(0); } 
            return flight; 
        }); 
    }
    
    public Flight updateFlightStatus(Integer flightId, Flight.FlightStatus status) { 
        return flightRepository.findById(flightId).map(flight -> { 
            flight.setStatus(status); 
            return flightRepository.save(flight); 
        }).orElse(null); 
    }
    
    public void deleteFlight(Integer id) { flightRepository.deleteById(id); }

    public Flight saveFlight(Flight flight) {
        if (flight.getCreatedAt() == null) {
            flight.setCreatedAt(LocalDateTime.now());
        }
        if (flight.getBusinessSeats() != null && flight.getEconomySeats() != null) {
            flight.setTotalSeats(flight.getBusinessSeats() + flight.getEconomySeats());
        }
        return flightRepository.save(flight);
    }
    
    @Transactional
    public Flight createFlight(Flight flightRequest) {
        Airline airline = airlineRepository.findById(flightRequest.getAirline().getAirlineId())
            .orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + flightRequest.getAirline().getAirlineId() + " 的航空公司"));
        
        Airport departureAirport = airportRepository.findById(flightRequest.getDepartureAirport().getAirportCode())
            .orElseThrow(() -> new ResourceNotFoundException("找不到代码为 " + flightRequest.getDepartureAirport().getAirportCode() + " 的出发机场"));

        Airport arrivalAirport = airportRepository.findById(flightRequest.getArrivalAirport().getAirportCode())
            .orElseThrow(() -> new ResourceNotFoundException("找不到代码为 " + flightRequest.getArrivalAirport().getAirportCode() + " 的到达机场"));

        Flight newFlight = new Flight();
        String finalFlightNumber = airline.getAirlineCode() + flightRequest.getFlightNumber();
        newFlight.setFlightNumber(finalFlightNumber);
        
        newFlight.setAircraftModel(flightRequest.getAircraftModel());
        newFlight.setDepartureTime(flightRequest.getDepartureTime());
        newFlight.setArrivalTime(flightRequest.getArrivalTime());
        newFlight.setEconomySeats(flightRequest.getEconomySeats());
        newFlight.setEconomySeatPrice(flightRequest.getEconomySeatPrice());
        newFlight.setBusinessSeats(flightRequest.getBusinessSeats());
        newFlight.setBusinessSeatPrice(flightRequest.getBusinessSeatPrice());
        
        newFlight.setAirline(airline);
        newFlight.setDepartureAirport(departureAirport);
        newFlight.setArrivalAirport(arrivalAirport);
        
        newFlight.setStatus(Flight.FlightStatus.PLANNED);

        return this.saveFlight(newFlight);
    }
}