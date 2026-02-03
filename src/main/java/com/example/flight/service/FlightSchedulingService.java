package com.example.flight.service;

import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightRoute;
import com.example.flight.repository.FlightRepository;
import com.example.flight.repository.FlightRouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightSchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(FlightSchedulingService.class);

    private final FlightRouteRepository flightRouteRepository;
    private final FlightRepository flightRepository;
    private final FlightService flightService;
    private final FlightSeatAvailabilityService availabilityService;


    public FlightSchedulingService(FlightRouteRepository flightRouteRepository,
                                   FlightRepository flightRepository,
                                   FlightService flightService,
                                   FlightSeatAvailabilityService availabilityService) {
        this.flightRouteRepository = flightRouteRepository;
        this.flightRepository = flightRepository;
        this.flightService = flightService;
        this.availabilityService = availabilityService;
    }

    /**
     * 每天凌晨1点执行。
     * cron表达式: 秒 分 时 日 月 周
     * "0 0 1 * * ?" 表示每天的1点0分0秒执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void manageDailyFlightSchedule() {
        logger.info("开始执行每日航班计划管理任务...");
        deleteOldFlights();
        generateFutureFlights();
        logger.info("每日航班计划管理任务执行完毕。");
    }

    /**
     * 删除两周前的航班记录
     */
    public void deleteOldFlights() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        logger.info("准备删除 {} 之前的过期航班...", twoWeeksAgo);
        long deletedCount = flightRepository.deleteByDepartureTimeBefore(twoWeeksAgo);
        logger.info("成功删除了 {} 条过期航班记录。", deletedCount);
    }

    /**
     * 检查并生成未来两周的航班
     */
    public void generateFutureFlights() {
        List<FlightRoute> activeRoutes = flightRouteRepository.findByActive(true);
        if (activeRoutes.isEmpty()) {
            logger.warn("没有找到任何启用的航线，无法生成航班。");
            return;
        }

        logger.info("找到 {} 条启用的航线，开始生成未来航班...", activeRoutes.size());
        LocalDate twoWeeksLater = LocalDate.now().plusWeeks(2);

        for (FlightRoute route : activeRoutes) {
            String fullFlightNumber = route.getAirline().getAirlineCode() + route.getFlightNumber();

            Optional<Flight> lastFlightOpt = flightRepository.findTopByFlightNumberAndDepartureAirportAndArrivalAirportOrderByDepartureTimeDesc(
                    fullFlightNumber, route.getDepartureAirport(), route.getArrivalAirport()
            );

            LocalDate startDate = lastFlightOpt.map(flight -> flight.getDepartureTime().toLocalDate())
                                               .orElse(LocalDate.now().minusDays(1)); // 如果没有，从昨天开始算，确保今天被包含

            // 从 last flight 的3天后开始生成，直到2周后
            for (LocalDate date = startDate.plusDays(3); date.isBefore(twoWeeksLater.plusDays(1)); date = date.plusDays(3)) {
                createFlightFromRoute(route, date);
            }
        }
    }

    private void createFlightFromRoute(FlightRoute route, LocalDate date) {
        Flight newFlight = new Flight();
        
        // 拼接完整的航班号
        newFlight.setFlightNumber(route.getAirline().getAirlineCode() + route.getFlightNumber());

        // 设置起飞和降落时间
        LocalDateTime departureDateTime = date.atTime(route.getDepartureTime());
        LocalDateTime arrivalDateTime = departureDateTime.plusMinutes(route.getFlightDurationMinutes());
        newFlight.setDepartureTime(departureDateTime);
        newFlight.setArrivalTime(arrivalDateTime);

        // 从航线模板复制属性
        newFlight.setAirline(route.getAirline());
        newFlight.setDepartureAirport(route.getDepartureAirport());
        newFlight.setArrivalAirport(route.getArrivalAirport());
        newFlight.setAircraftModel(route.getAircraftModel());
        newFlight.setBusinessSeats(route.getBusinessSeats());
        newFlight.setBusinessSeatPrice(route.getBusinessSeatPrice());
        newFlight.setEconomySeats(route.getEconomySeats());
        newFlight.setEconomySeatPrice(route.getEconomySeatPrice());
        newFlight.setStatus(Flight.FlightStatus.PLANNED);
        
        // 保存航班并初始化座位
        Flight savedFlight = flightService.saveFlight(newFlight);
        availabilityService.initializeAvailability(savedFlight);

        logger.info("为航线 {} 在 {} 创建了新航班, ID: {}", newFlight.getFlightNumber(), date, savedFlight.getFlightId());
    }
}