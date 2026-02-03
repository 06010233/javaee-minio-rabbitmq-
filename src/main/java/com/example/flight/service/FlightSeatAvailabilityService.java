package com.example.flight.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightSeatAvailability;
import com.example.flight.entity.Order;
import com.example.flight.repository.FlightSeatAvailabilityRepository;

@Service
public class FlightSeatAvailabilityService {

    private final FlightSeatAvailabilityRepository availabilityRepository;

    @Autowired
    public FlightSeatAvailabilityService(FlightSeatAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    // [已添加] 确保这个 public 方法存在，以供 Controller 调用
    public FlightSeatAvailability saveAvailability(FlightSeatAvailability availability) {
        availability.setLastUpdated(LocalDateTime.now());
        return availabilityRepository.save(availability);
    }

    public List<FlightSeatAvailability> findAll() {
        return availabilityRepository.findAll();
    }

    public Optional<FlightSeatAvailability> findByFlightId(Integer flightId) {
        return availabilityRepository.findById(flightId);
    }

    @Transactional
    public void initializeAvailability(Flight flight) {
        FlightSeatAvailability availability = new FlightSeatAvailability();
        availability.setFlightId(flight.getFlightId());
        availability.setFlight(flight);
        availability.setAvailableBusinessSeats(flight.getBusinessSeats());
        availability.setAvailableEconomySeats(flight.getEconomySeats());
        this.saveAvailability(availability); // 内部调用 saveAvailability
    }

    @Transactional
    public boolean updateSeatAvailability(Integer flightId, Order.SeatClass seatClass, int delta) {
        Optional<FlightSeatAvailability> availabilityOpt = this.findByFlightId(flightId);
        
        if (availabilityOpt.isPresent()) {
            FlightSeatAvailability availability = availabilityOpt.get();
            Flight flight = availability.getFlight();
            
            if (seatClass == Order.SeatClass.BUSINESS) {
                int newSeats = availability.getAvailableBusinessSeats() + delta;
                if (newSeats < 0 || newSeats > flight.getBusinessSeats()) return false;
                availability.setAvailableBusinessSeats(newSeats);
            } else {
                int newSeats = availability.getAvailableEconomySeats() + delta;
                if (newSeats < 0 || newSeats > flight.getEconomySeats()) return false;
                availability.setAvailableEconomySeats(newSeats);
            }
            
            this.saveAvailability(availability); // 内部调用 saveAvailability
            return true;
        }
        return false;
    }

    public boolean checkSeatAvailability(Integer flightId, Order.SeatClass seatClass, int requiredSeats) {
        return this.findByFlightId(flightId).map(availability -> {
            if (seatClass == Order.SeatClass.BUSINESS) {
                return availability.getAvailableBusinessSeats() >= requiredSeats;
            } else {
                return availability.getAvailableEconomySeats() >= requiredSeats;
            }
        }).orElse(false);
    }
    
    @Transactional
    public void deleteByFlightId(Integer flightId) {
        if (availabilityRepository.existsById(flightId)) {
            availabilityRepository.deleteById(flightId);
        }
    }

    public List<FlightSeatAvailability> findWithAvailableBusinessSeats() {
        return availabilityRepository.findWithAvailableBusinessSeats();
    }

    public List<FlightSeatAvailability> findWithAvailableEconomySeats() {
        return availabilityRepository.findWithAvailableEconomySeats();
    }
}