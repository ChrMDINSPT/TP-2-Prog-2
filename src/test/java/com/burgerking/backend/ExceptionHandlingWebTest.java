package com.burgerking.backend;

import com.burgerking.backend.controller.ItemController;
import com.burgerking.backend.controller.EmployeeController;
import com.burgerking.backend.controller.IngredientController;
import com.burgerking.backend.controller.OrderController;
import com.burgerking.backend.controller.SalesController;
import com.burgerking.backend.controller.UserController;
import com.burgerking.backend.dto.CreateItemRequest;
import com.burgerking.backend.dto.CreateOrderRequest;
import com.burgerking.backend.dto.UpdateUserRequest;
import com.burgerking.backend.entity.User;
import com.burgerking.backend.exception.ConflictException;
import com.burgerking.backend.exception.GlobalExceptionHandler;
import com.burgerking.backend.service.EmployeeService;
import com.burgerking.backend.service.IngredientService;
import com.burgerking.backend.service.ItemService;
import com.burgerking.backend.service.OrderService;
import com.burgerking.backend.service.SalesService;
import com.burgerking.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.ArgumentCaptor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class ExceptionHandlingWebTest {

    private UserService userService;
    private ItemService itemService;
    private OrderService orderService;
    private SalesService salesService;
    private EmployeeService employeeService;
    private IngredientService ingredientService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = org.mockito.Mockito.mock(UserService.class);
        itemService = org.mockito.Mockito.mock(ItemService.class);
        orderService = org.mockito.Mockito.mock(OrderService.class);
        salesService = org.mockito.Mockito.mock(SalesService.class);
        employeeService = mock(EmployeeService.class);
        ingredientService = mock(IngredientService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new UserController(userService),
                        new ItemController(itemService),
                        new OrderController(orderService),
                        new SalesController(salesService),
                        new EmployeeController(employeeService),
                        new IngredientController(ingredientService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void requiredCreateUserFieldsReturnValidationDetails() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("externalSubject"),
                                org.hamcrest.Matchers.containsString("name"),
                                org.hamcrest.Matchers.containsString("userType"))));
        verify(userService, never()).createUser(any());
    }

    @Test
    void nameOnlyUserUpdateAcceptsLegacyExtraFields() throws Exception {
        User user = new User();
        user.setName("Nuevo nombre");
        when(userService.updateUser(anyLong(), any())).thenReturn(user);

        mockMvc.perform(put("/api/users/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"externalSubject\":\"legacy\",\"name\":\"Nuevo nombre\",\"userType\":\"MANAGER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nuevo nombre"));

        ArgumentCaptor<UpdateUserRequest> request = ArgumentCaptor.forClass(UpdateUserRequest.class);
        verify(userService).updateUser(eq(7L), request.capture());
        assertEquals("Nuevo nombre", request.getValue().getName());
    }

    @Test
    void minimalUserUpdatePayloadIsAccepted() throws Exception {
        User user = new User();
        user.setName("Nombre mínimo");
        when(userService.updateUser(anyLong(), any())).thenReturn(user);

        mockMvc.perform(put("/api/users/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nombre mínimo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nombre mínimo"));
    }

    @Test
    void missingUserUpdateNameReturnsValidationDetails() throws Exception {
        mockMvc.perform(put("/api/users/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("name")));
        verify(userService, never()).updateUser(anyLong(), any(UpdateUserRequest.class));
    }

    @Test
    void omittedIngredientIdsAreRejectedBeforeItemService() throws Exception {
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hamburguesa\",\"price\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("ingredientIds")));
        verify(itemService, never()).createItem(any(CreateItemRequest.class));
    }

    @Test
    void nullOrderItemsAreRejectedBeforeOrderService() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sellerId\":1,\"items\":[null]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("items")));
        verify(orderService, never()).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void missingEmployeeRoleReturnsValidationDetails() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/employees/4/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("role")));
        verify(employeeService, never()).changeDailyRole(anyLong(), any());
    }

    @Test
    void missingIngredientNameReturnsValidationDetails() throws Exception {
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("name")));
        verify(ingredientService, never()).createIngredient(any());

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest());
        verify(ingredientService, never()).createIngredient(any());
    }

    @Test
    void malformedJsonKeepsFrameworkBadRequestStatus() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingQueryParameterKeepsFrameworkBadRequestStatus() throws Exception {
        mockMvc.perform(get("/api/sales/range"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void unsupportedMethodPreservesAllowHeaderAndStatus() throws Exception {
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", org.hamcrest.Matchers.containsString("POST")))
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void unexpectedExceptionUsesSafeInternalError() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new IllegalStateException("secret db details"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ocurrió un error interno en el servidor"));
    }

    @Test
    void frameworkServerErrorUsesSafeInternalError() throws Exception {
        when(userService.getUserById(99L)).thenThrow(
                new HttpMessageNotWritableException("private serializer diagnostics"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(
                        "Ocurrió un error interno en el servidor"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("private"))));
    }

    @Test
    void dataIntegrityViolationUsesSafeConflictMessage() throws Exception {
        when(userService.getUserById(99L))
                .thenThrow(new DataIntegrityViolationException("raw SQL constraint details"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La operación entra en conflicto con un recurso existente o relacionado"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("raw SQL"))));
    }

    @Test
    void customConflictRemainsConflict() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new ConflictException("conflicto"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("conflicto"));
    }
}
