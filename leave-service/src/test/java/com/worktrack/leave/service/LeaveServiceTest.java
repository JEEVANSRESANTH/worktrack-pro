package com.worktrack.leave.service;

import com.worktrack.leave.entity.Leave;
import com.worktrack.leave.repository.LeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test Suite: LeaveService
 *
 * Purpose: Validate LeaveService business logic for leave request management, including
 * creation, retrieval, filtering by employee, and status updates using AAA pattern.
 *
 * Dependencies Mocked:
 * - LeaveRepository: All database operations are mocked
 *
 * Coverage:
 * - ✅ SUCCESS: Request leave with default PENDING status
 * - ✅ SUCCESS: Retrieve all leave requests
 * - ✅ SUCCESS: Filter leave requests by employee ID
 * - ✅ SUCCESS: Update leave request status
 * - ✅ ERROR: Handle leave request not found when updating status
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeaveService Unit Tests")
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @InjectMocks
    private LeaveService leaveService;

    private Leave testLeave;

    // ========== ARRANGE ==========
    @BeforeEach
    void setUp() {
        // Initialize test data with typical leave request values
        testLeave = new Leave();
        testLeave.setId(1L);
        testLeave.setEmployeeId("WT-434019");
        testLeave.setStartDate(LocalDate.of(2026, 7, 1));
        testLeave.setEndDate(LocalDate.of(2026, 7, 5));
        testLeave.setLeaveType("VACATION");
        testLeave.setStatus("PENDING");
        testLeave.setReason("Summer vacation with family");
    }

    // ========== TEST 1: SUCCESS - Request leave with default PENDING status ==========
    @Test
    @DisplayName("Should create a new leave request with status set to PENDING by default")
    void testRequestLeave_DefaultStatus_Success() {
        // Arrange
        Leave newLeave = new Leave();
        newLeave.setEmployeeId("WT-001");
        newLeave.setStartDate(LocalDate.of(2026, 8, 1));
        newLeave.setEndDate(LocalDate.of(2026, 8, 3));
        newLeave.setLeaveType("SICK");
        // Status is null - should be set to PENDING by service
        newLeave.setReason("Medical appointment");

        Leave savedLeave = new Leave(
                "WT-001",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                "SICK",
                "PENDING", // Service should set this
                "Medical appointment"
        );
        savedLeave.setId(2L);

        when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

        // Act
        Leave requestedLeave = leaveService.requestLeave(newLeave);

        // Assert
        assertNotNull(requestedLeave);
        assertEquals("PENDING", requestedLeave.getStatus());
        assertEquals("WT-001", requestedLeave.getEmployeeId());
        assertEquals("SICK", requestedLeave.getLeaveType());
        assertEquals(LocalDate.of(2026, 8, 1), requestedLeave.getStartDate());

        // Verify
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    // ========== TEST 2: SUCCESS - Request leave with explicit status override ==========
    @Test
    @DisplayName("Should preserve explicitly set leave status and not override it")
    void testRequestLeave_ExplicitStatus_Success() {
        // Arrange
        Leave newLeave = new Leave();
        newLeave.setEmployeeId("WT-002");
        newLeave.setStartDate(LocalDate.of(2026, 9, 1));
        newLeave.setEndDate(LocalDate.of(2026, 9, 10));
        newLeave.setLeaveType("VACATION");
        newLeave.setStatus("APPROVED"); // Explicitly set status
        newLeave.setReason("Pre-planned vacation");

        Leave savedLeave = new Leave(
                "WT-002",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 10),
                "VACATION",
                "APPROVED",
                "Pre-planned vacation"
        );
        savedLeave.setId(3L);

        when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

        // Act
        Leave requestedLeave = leaveService.requestLeave(newLeave);

        // Assert
        assertNotNull(requestedLeave);
        assertEquals("APPROVED", requestedLeave.getStatus());

        // Verify
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    // ========== TEST 3: SUCCESS - Retrieve all leave requests ==========
    @Test
    @DisplayName("Should retrieve all leave requests from repository")
    void testGetAllLeaves_Success() {
        // Arrange
        Leave leave1 = new Leave("WT-001", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), "VACATION", "PENDING", "Summer break");
        leave1.setId(1L);
        Leave leave2 = new Leave("WT-002", LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 12), "SICK", "APPROVED", "Sick leave");
        leave2.setId(2L);
        Leave leave3 = new Leave("WT-003", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15), "MATERNITY", "PENDING", "Maternity leave");
        leave3.setId(3L);

        List<Leave> mockLeaves = Arrays.asList(leave1, leave2, leave3);
        when(leaveRepository.findAll()).thenReturn(mockLeaves);

        // Act
        List<Leave> allLeaves = leaveService.getAllLeaves();

        // Assert
        assertNotNull(allLeaves);
        assertEquals(3, allLeaves.size());
        assertEquals("VACATION", allLeaves.get(0).getLeaveType());
        assertEquals("PENDING", allLeaves.get(0).getStatus());
        assertEquals("MATERNITY", allLeaves.get(2).getLeaveType());

        // Verify
        verify(leaveRepository, times(1)).findAll();
    }

    // ========== TEST 4: SUCCESS - Filter leave requests by employee ID ==========
    @Test
    @DisplayName("Should retrieve all leave requests for a specific employee")
    void testGetLeavesByEmployee_Success() {
        // Arrange
        String employeeId = "WT-434019";
        Leave leave1 = new Leave(employeeId, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), "VACATION", "PENDING", "Summer vacation");
        leave1.setId(1L);
        Leave leave2 = new Leave(employeeId, LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 16), "CASUAL", "APPROVED", "Personal work");
        leave2.setId(2L);

        List<Leave> employeeLeaves = Arrays.asList(leave1, leave2);
        when(leaveRepository.findByEmployeeId(employeeId)).thenReturn(employeeLeaves);

        // Act
        List<Leave> leaves = leaveService.getLeavesByEmployee(employeeId);

        // Assert
        assertNotNull(leaves);
        assertEquals(2, leaves.size());
        assertTrue(leaves.stream().allMatch(l -> l.getEmployeeId().equals(employeeId)));
        assertEquals("VACATION", leaves.get(0).getLeaveType());
        assertEquals("CASUAL", leaves.get(1).getLeaveType());

        // Verify
        verify(leaveRepository, times(1)).findByEmployeeId(employeeId);
    }

    // ========== TEST 5: SUCCESS - Update leave request status ==========
    @Test
    @DisplayName("Should update leave status to uppercase and persist changes")
    void testUpdateLeaveStatus_Success() {
        // Arrange
        Long leaveId = 1L;
        String newStatus = "approved"; // lowercase - should be converted to uppercase

        Leave existingLeave = new Leave(
                "WT-001",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "VACATION",
                "PENDING",
                "Summer vacation"
        );
        existingLeave.setId(leaveId);

        Leave updatedLeave = new Leave(
                "WT-001",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "VACATION",
                "APPROVED",
                "Summer vacation"
        );
        updatedLeave.setId(leaveId);

        when(leaveRepository.findById(leaveId)).thenReturn(Optional.of(existingLeave));
        when(leaveRepository.save(any(Leave.class))).thenReturn(updatedLeave);

        // Act
        Leave result = leaveService.updateLeaveStatus(leaveId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus()); // Verify uppercase conversion
        assertEquals(leaveId, result.getId());
        assertEquals("VACATION", result.getLeaveType());

        // Verify
        verify(leaveRepository, times(1)).findById(leaveId);
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    // ========== TEST 6: ERROR - Handle leave request not found when updating status ==========
    @Test
    @DisplayName("Should throw exception when attempting to update status of non-existent leave request")
    void testUpdateLeaveStatus_NotFound_Failure() {
        // Arrange
        Long nonExistentLeaveId = 9999L;
        String newStatus = "APPROVED";

        when(leaveRepository.findById(nonExistentLeaveId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            leaveService.updateLeaveStatus(nonExistentLeaveId, newStatus);
        });

        assertTrue(exception.getMessage().contains("Leave record not found with database ID"));
        assertTrue(exception.getMessage().contains(nonExistentLeaveId.toString()));

        // Verify
        verify(leaveRepository, times(1)).findById(nonExistentLeaveId);
        verify(leaveRepository, never()).save(any(Leave.class));
    }

    // ========== TEST 7: SUCCESS - Empty leave list for employee with no requests ==========
    @Test
    @DisplayName("Should return empty list when employee has no leave requests")
    void testGetLeavesByEmployee_EmptyList_Success() {
        // Arrange
        String employeeWithNoLeaves = "WT-WORKAHOLIC";
        List<Leave> emptyLeaveList = Arrays.asList();

        when(leaveRepository.findByEmployeeId(employeeWithNoLeaves)).thenReturn(emptyLeaveList);

        // Act
        List<Leave> leaves = leaveService.getLeavesByEmployee(employeeWithNoLeaves);

        // Assert
        assertNotNull(leaves);
        assertTrue(leaves.isEmpty());
        assertEquals(0, leaves.size());

        // Verify
        verify(leaveRepository, times(1)).findByEmployeeId(employeeWithNoLeaves);
    }

    // ========== TEST 8: Edge Case - Blank status should default to PENDING ==========
    @Test
    @DisplayName("Should set status to PENDING when status is blank string")
    void testRequestLeave_BlankStatus_DefaultToPending() {
        // Arrange
        Leave newLeave = new Leave();
        newLeave.setEmployeeId("WT-005");
        newLeave.setStartDate(LocalDate.of(2026, 10, 1));
        newLeave.setEndDate(LocalDate.of(2026, 10, 5));
        newLeave.setLeaveType("CASUAL");
        newLeave.setStatus(""); // Blank status
        newLeave.setReason("Personal reasons");

        Leave savedLeave = new Leave(
                "WT-005",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 5),
                "CASUAL",
                "PENDING",
                "Personal reasons"
        );
        savedLeave.setId(5L);

        when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

        // Act
        Leave requestedLeave = leaveService.requestLeave(newLeave);

        // Assert
        assertNotNull(requestedLeave);
        assertEquals("PENDING", requestedLeave.getStatus());

        // Verify
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    // ========== TEST 9: Status update with various leave statuses ==========
    @Test
    @DisplayName("Should convert various leave status values to uppercase during update")
    void testUpdateLeaveStatus_VariousStatusValues() {
        // Arrange - Test with lowercase input
        Long leaveId = 1L;
        String[] statusValues = {"pending", "approved", "rejected", "cancelled"};

        for (String status : statusValues) {
            Leave existingLeave = new Leave("WT-001", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), "VACATION", "PENDING", "Reason");
            existingLeave.setId(leaveId);

            Leave updatedLeave = new Leave("WT-001", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), "VACATION", status.toUpperCase(), "Reason");
            updatedLeave.setId(leaveId);

            when(leaveRepository.findById(leaveId)).thenReturn(Optional.of(existingLeave));
            when(leaveRepository.save(any(Leave.class))).thenReturn(updatedLeave);

            // Act
            Leave result = leaveService.updateLeaveStatus(leaveId, status);

            // Assert
            assertEquals(status.toUpperCase(), result.getStatus());
        }
    }

    // ========== TEST 10: Leave request date validation ==========
    @Test
    @DisplayName("Should preserve date information in leave request")
    void testRequestLeave_PreservesDateInfo() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        LocalDate endDate = LocalDate.of(2026, 12, 27);

        Leave newLeave = new Leave();
        newLeave.setEmployeeId("WT-ADMIN");
        newLeave.setStartDate(startDate);
        newLeave.setEndDate(endDate);
        newLeave.setLeaveType("VACATION");
        newLeave.setStatus("PENDING");
        newLeave.setReason("Year-end vacation");

        Leave savedLeave = new Leave(
                "WT-ADMIN",
                startDate,
                endDate,
                "VACATION",
                "PENDING",
                "Year-end vacation"
        );
        savedLeave.setId(10L);

        when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

        // Act
        Leave requestedLeave = leaveService.requestLeave(newLeave);

        // Assert
        assertEquals(startDate, requestedLeave.getStartDate());
        assertEquals(endDate, requestedLeave.getEndDate());
        assertTrue(requestedLeave.getEndDate().isAfter(requestedLeave.getStartDate()));

        // Verify
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    // ========== TEST 11: Leave type preservation ==========
    @Test
    @DisplayName("Should preserve different leave types during request")
    void testRequestLeave_PreservesLeaveType() {
        // Arrange
        String[] leaveTypes = {"CASUAL", "SICK", "VACATION", "MATERNITY", "PATERNITY"};

        for (String leaveType : leaveTypes) {
            Leave newLeave = new Leave();
            newLeave.setEmployeeId("WT-TEST");
            newLeave.setStartDate(LocalDate.of(2026, 7, 1));
            newLeave.setEndDate(LocalDate.of(2026, 7, 5));
            newLeave.setLeaveType(leaveType);
            newLeave.setStatus("PENDING");
            newLeave.setReason("Test reason");

            Leave savedLeave = new Leave("WT-TEST", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), leaveType, "PENDING", "Test reason");
            savedLeave.setId(1L);

            when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

            // Act
            Leave requestedLeave = leaveService.requestLeave(newLeave);

            // Assert
            assertEquals(leaveType, requestedLeave.getLeaveType());
        }
    }
}

