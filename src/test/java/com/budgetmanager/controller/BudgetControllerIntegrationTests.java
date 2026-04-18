package com.budgetmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

import com.budgetmanager.controller.dto.AddCategoryRequest;
import com.budgetmanager.controller.dto.SetBudgetRequest;
import com.budgetmanager.controller.dto.SpendingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * BudgetController Integration Tests
 * Tests all budget management endpoints
 * Demonstrates Strategy Pattern, Observer Pattern, and Composite Pattern
 */
@SpringBootTest
@Transactional
public class BudgetControllerIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

        private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private SetBudgetRequest setBudgetRequest;
    private SpendingRequest spendingRequest;
    private AddCategoryRequest addCategoryRequest;

    @BeforeEach
    public void setUp() {
        if (mockMvc == null) {
            mockMvc = webAppContextSetup(context).build();
        }
        setBudgetRequest = new SetBudgetRequest(1000.0, "MONTHLY");
        spendingRequest = new SpendingRequest(150.0, "Food");
        addCategoryRequest = new AddCategoryRequest("Food", 300.0);
    }

    /**
     * Test: Set Budget - Successfully set budget for user
     */
    @Test
    public void testSetBudget_Success() throws Exception {
        Long userId = 1L;
        
        mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.totalBudget").value(1000.0))
                .andExpect(jsonPath("$.period").value("MONTHLY"))
                .andExpect(jsonPath("$.totalSpent").value(0.0))
                .andExpect(jsonPath("$.remainingBudget").value(1000.0))
                .andExpect(jsonPath("$.isExceeded").value(false));
    }

    /**
     * Test: Set Budget - Negative amount should fail
     */
    @Test
    public void testSetBudget_NegativeAmount() throws Exception {
        Long userId = 1L;
        SetBudgetRequest invalidRequest = new SetBudgetRequest(-500.0, "MONTHLY");
        
        mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Get Budget - Retrieve budget by user and period
     */
    @Test
    public void testGetBudget_Success() throws Exception {
        Long userId = 1L;
        
        // First, set budget
        mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk());

        // Then retrieve it
        mockMvc.perform(get("/api/budget/" + userId)
                .param("period", "MONTHLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudget").value(1000.0))
                .andExpect(jsonPath("$.period").value("MONTHLY"));
    }

    /**
     * Test: Get Remaining Budget
     */
    @Test
    public void testGetRemainingBudget() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(get("/api/budget/" + budgetId + "/remaining"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    /**
     * Test: Add Category to Budget - Composite Pattern
     */
    @Test
    public void testAddCategory_Success() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(post("/api/budget/" + budgetId + "/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.budgetLimit").value(300.0))
                .andExpect(jsonPath("$.spent").value(0.0))
                .andExpect(jsonPath("$.isExceeded").value(false));
    }

    /**
     * Test: Get Categories - Retrieve all categories for a budget
     */
    @Test
    public void testGetCategories() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(post("/api/budget/" + budgetId + "/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/budget/" + budgetId + "/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"));
    }

    /**
     * Test: Record Spending - Updates both parent budget and category
     * Composite Pattern: Demonstrates how spending impacts hierarchy
     */
    @Test
    public void testRecordSpending_Success() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(post("/api/budget/" + budgetId + "/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addCategoryRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/budget/" + budgetId + "/spend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(spendingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent").value(150.0))
                .andExpect(jsonPath("$.remainingBudget").value(850.0))
                .andExpect(jsonPath("$.percentageUsed").value(15.0));
    }

    /**
     * Test: Check Budget - Default behavior (Monthly Strategy)
     * Strategy Pattern: Uses MonthlyBudgetStrategy by default
     */
    @Test
    public void testCheckBudget_WithinLimit() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(post("/api/budget/" + budgetId + "/check"))
                .andExpect(status().isOk());
    }

    /**
     * Test: Set Monthly Strategy - Strategy Pattern
     */
    @Test
    public void testSetMonthlyStrategy() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(post("/api/budget/" + budgetId + "/strategy/monthly"))
                .andExpect(status().isOk())
                .andExpect(content().string("Monthly budget strategy activated"));
    }

    /**
     * Test: Set Category Strategy - Strategy Pattern
     */
    @Test
    public void testSetCategoryStrategy() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(post("/api/budget/" + budgetId + "/strategy/category/Food"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category-based budget strategy activated for: Food"));
    }

    /**
     * Test: Delete Budget
     */
    @Test
    public void testDeleteBudget() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        mockMvc.perform(delete("/api/budget/" + budgetId))
                .andExpect(status().isOk());
    }

    /**
     * Test: Percentage Used Calculation
     */
    @Test
    public void testPercentageUsed() throws Exception {
        Long userId = 1L;
        SetBudgetRequest request = new SetBudgetRequest(500.0, "MONTHLY");
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        // Spend 250 (50% of 500)
        SpendingRequest spendRequest = new SpendingRequest(250.0, "");
        mockMvc.perform(post("/api/budget/" + budgetId + "/spend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(spendRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentageUsed").value(50.0));
    }

    /**
     * Test: Multiple Categories tracking
     * Composite Pattern: Multiple categories within one budget
     */
    @Test
    public void testMultipleCategories() throws Exception {
        Long userId = 1L;
        
        String setResponse = mockMvc.perform(post("/api/budget/set")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setBudgetRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long budgetId = objectMapper.readTree(setResponse).path("id").asLong();

        AddCategoryRequest foodCategory = new AddCategoryRequest("Food", 300.0);
        AddCategoryRequest transportCategory = new AddCategoryRequest("Transport", 200.0);

        mockMvc.perform(post("/api/budget/" + budgetId + "/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(foodCategory)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/budget/" + budgetId + "/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transportCategory)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/budget/" + budgetId + "/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[1].name").value("Transport"));
    }
}
