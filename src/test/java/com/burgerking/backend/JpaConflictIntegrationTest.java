package com.burgerking.backend;

import com.burgerking.backend.entity.Employee;
import com.burgerking.backend.entity.Ingredient;
import com.burgerking.backend.entity.Item;
import com.burgerking.backend.entity.Order;
import com.burgerking.backend.entity.OrderStatus;
import com.burgerking.backend.entity.User;
import com.burgerking.backend.entity.UserType;
import com.burgerking.backend.repository.IngredientRepository;
import com.burgerking.backend.repository.ItemRepository;
import com.burgerking.backend.repository.EmployeeRepository;
import com.burgerking.backend.repository.OrderRepository;
import com.burgerking.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class JpaConflictIntegrationTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoSpyBean
    private UserRepository userRepositorySpy;

    @Test
    @Transactional
    void duplicateIngredientNameViolatesUniqueConstraint() {
        ingredientRepository.saveAndFlush(new Ingredient("queso"));

        assertThrows(DataIntegrityViolationException.class,
                () -> ingredientRepository.saveAndFlush(new Ingredient("queso")));
    }

    @Test
    @Transactional
    void ingredientReferencedByItemCannotBeDeleted() {
        ingredientRepository.saveAndFlush(new Ingredient("lechuga"));
        Ingredient ingredient = ingredientRepository.findAll().getFirst();
        Item item = new Item();
        item.setName("Ensalada");
        item.setIngredients(java.util.Set.of(ingredient));
        itemRepository.saveAndFlush(item);

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("delete from ingredients where id = ?", ingredient.getId()));
    }

    @Test
    void duplicateIngredientThroughApiReturnsSafeConflict() throws Exception {
        String name = "api-queso-" + UUID.randomUUID();
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La operación entra en conflicto con un recurso existente o relacionado"));
    }

    @Test
    void referencedIngredientDeleteThroughApiReturnsConflictAndKeepsRecord() throws Exception {
        Ingredient ingredient = ingredientRepository.saveAndFlush(
                new Ingredient("api-lechuga-" + UUID.randomUUID()));
        Item item = new Item();
        item.setName("api-ensalada-" + UUID.randomUUID());
        item.setIngredients(java.util.Set.of(ingredientRepository.findById(ingredient.getId()).orElseThrow()));
        itemRepository.saveAndFlush(item);

        mockMvc.perform(delete("/api/ingredients/" + ingredient.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La operación entra en conflicto con un recurso existente o relacionado"));
        org.junit.jupiter.api.Assertions.assertTrue(ingredientRepository.existsById(ingredient.getId()));
    }

    @Test
    void referencedUserDeleteThroughApiReturnsConflict() throws Exception {
        String subject = "api-user-" + UUID.randomUUID();
        User user = new User();
        user.setExternalSubject(subject);
        user.setName("Usuario API");
        user.setUserType(UserType.EMPLOYEE);
        userRepositorySpy.saveAndFlush(user);

        Employee employee = new Employee();
        employee.setUser(userRepositorySpy.findById(user.getId()).orElseThrow());
        employeeRepository.saveAndFlush(employee);

        Order order = new Order();
        order.setSeller(employeeRepository.findById(user.getId()).orElseThrow());
        order.setStatus(OrderStatus.RECEIVED);
        orderRepository.saveAndFlush(order);

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La operación entra en conflicto con un recurso existente o relacionado"));
        org.junit.jupiter.api.Assertions.assertTrue(userRepositorySpy.existsById(user.getId()));
    }

    @Test
    void staleUserDuplicatePrecheckFallsBackToDatabaseConflict() throws Exception {
        String subject = "stale-user-" + UUID.randomUUID();
        User user = new User();
        user.setExternalSubject(subject);
        user.setName("Usuario existente");
        user.setUserType(UserType.MANAGER);
        userRepositorySpy.saveAndFlush(user);

        doReturn(false).when(userRepositorySpy).existsByExternalSubject(subject);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"externalSubject\":\"" + subject
                                + "\",\"name\":\"Usuario duplicado\",\"userType\":\"MANAGER\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "La operación entra en conflicto con un recurso existente o relacionado"));
    }
}
