package org.mgoulene.web.rest;

import org.mgoulene.MyaccountApp;

import org.mgoulene.domain.BudgetItem;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.domain.Category;
import org.mgoulene.domain.User;
import org.mgoulene.repository.BudgetItemRepository;
import org.mgoulene.service.BudgetItemService;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.mapper.BudgetItemMapper;
import org.mgoulene.web.rest.errors.ExceptionTranslator;
import org.mgoulene.service.dto.BudgetItemCriteria;
import org.mgoulene.service.BudgetItemQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static org.mgoulene.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BudgetItemResource REST controller.
 *
 * @see BudgetItemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyaccountApp.class)
public class BudgetItemResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private BudgetItemMapper budgetItemMapper;
    
    @Autowired
    private BudgetItemService budgetItemService;

    @Autowired
    private BudgetItemQueryService budgetItemQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBudgetItemMockMvc;

    private BudgetItem budgetItem;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BudgetItemResource budgetItemResource = new BudgetItemResource(budgetItemService, budgetItemQueryService);
        this.restBudgetItemMockMvc = MockMvcBuilders.standaloneSetup(budgetItemResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BudgetItem createEntity(EntityManager em) {
        BudgetItem budgetItem = new BudgetItem()
            .name(DEFAULT_NAME)
            .order(DEFAULT_ORDER);
        return budgetItem;
    }

    @Before
    public void initTest() {
        budgetItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createBudgetItem() throws Exception {
        int databaseSizeBeforeCreate = budgetItemRepository.findAll().size();

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);
        restBudgetItemMockMvc.perform(post("/api/budget-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isCreated());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeCreate + 1);
        BudgetItem testBudgetItem = budgetItemList.get(budgetItemList.size() - 1);
        assertThat(testBudgetItem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBudgetItem.getOrder()).isEqualTo(DEFAULT_ORDER);
    }

    @Test
    @Transactional
    public void createBudgetItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = budgetItemRepository.findAll().size();

        // Create the BudgetItem with an existing ID
        budgetItem.setId(1L);
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBudgetItemMockMvc.perform(post("/api/budget-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetItemRepository.findAll().size();
        // set the field null
        budgetItem.setName(null);

        // Create the BudgetItem, which fails.
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        restBudgetItemMockMvc.perform(post("/api/budget-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOrderIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetItemRepository.findAll().size();
        // set the field null
        budgetItem.setOrder(null);

        // Create the BudgetItem, which fails.
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        restBudgetItemMockMvc.perform(post("/api/budget-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBudgetItems() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList
        restBudgetItemMockMvc.perform(get("/api/budget-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }
    
    @Test
    @Transactional
    public void getBudgetItem() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get the budgetItem
        restBudgetItemMockMvc.perform(get("/api/budget-items/{id}", budgetItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(budgetItem.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name equals to DEFAULT_NAME
        defaultBudgetItemShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the budgetItemList where name equals to UPDATED_NAME
        defaultBudgetItemShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBudgetItemShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the budgetItemList where name equals to UPDATED_NAME
        defaultBudgetItemShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where name is not null
        defaultBudgetItemShouldBeFound("name.specified=true");

        // Get all the budgetItemList where name is null
        defaultBudgetItemShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order equals to DEFAULT_ORDER
        defaultBudgetItemShouldBeFound("order.equals=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order equals to UPDATED_ORDER
        defaultBudgetItemShouldNotBeFound("order.equals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByOrderIsInShouldWork() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order in DEFAULT_ORDER or UPDATED_ORDER
        defaultBudgetItemShouldBeFound("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER);

        // Get all the budgetItemList where order equals to UPDATED_ORDER
        defaultBudgetItemShouldNotBeFound("order.in=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order is not null
        defaultBudgetItemShouldBeFound("order.specified=true");

        // Get all the budgetItemList where order is null
        defaultBudgetItemShouldNotBeFound("order.specified=false");
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order greater than or equals to DEFAULT_ORDER
        defaultBudgetItemShouldBeFound("order.greaterOrEqualThan=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order greater than or equals to UPDATED_ORDER
        defaultBudgetItemShouldNotBeFound("order.greaterOrEqualThan=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    public void getAllBudgetItemsByOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        // Get all the budgetItemList where order less than or equals to DEFAULT_ORDER
        defaultBudgetItemShouldNotBeFound("order.lessThan=" + DEFAULT_ORDER);

        // Get all the budgetItemList where order less than or equals to UPDATED_ORDER
        defaultBudgetItemShouldBeFound("order.lessThan=" + UPDATED_ORDER);
    }


    @Test
    @Transactional
    public void getAllBudgetItemsByBudgetItemPeriodsIsEqualToSomething() throws Exception {
        // Initialize the database
        BudgetItemPeriod budgetItemPeriods = BudgetItemPeriodResourceIntTest.createEntity(em);
        em.persist(budgetItemPeriods);
        em.flush();
        budgetItem.addBudgetItemPeriods(budgetItemPeriods);
        budgetItemRepository.saveAndFlush(budgetItem);
        Long budgetItemPeriodsId = budgetItemPeriods.getId();

        // Get all the budgetItemList where budgetItemPeriods equals to budgetItemPeriodsId
        defaultBudgetItemShouldBeFound("budgetItemPeriodsId.equals=" + budgetItemPeriodsId);

        // Get all the budgetItemList where budgetItemPeriods equals to budgetItemPeriodsId + 1
        defaultBudgetItemShouldNotBeFound("budgetItemPeriodsId.equals=" + (budgetItemPeriodsId + 1));
    }


    @Test
    @Transactional
    public void getAllBudgetItemsByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        Category category = CategoryResourceIntTest.createEntity(em);
        em.persist(category);
        em.flush();
        budgetItem.setCategory(category);
        budgetItemRepository.saveAndFlush(budgetItem);
        Long categoryId = category.getId();

        // Get all the budgetItemList where category equals to categoryId
        defaultBudgetItemShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the budgetItemList where category equals to categoryId + 1
        defaultBudgetItemShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }


    @Test
    @Transactional
    public void getAllBudgetItemsByAccountIsEqualToSomething() throws Exception {
        // Initialize the database
        User account = UserResourceIntTest.createEntity(em);
        em.persist(account);
        em.flush();
        budgetItem.setAccount(account);
        budgetItemRepository.saveAndFlush(budgetItem);
        Long accountId = account.getId();

        // Get all the budgetItemList where account equals to accountId
        defaultBudgetItemShouldBeFound("accountId.equals=" + accountId);

        // Get all the budgetItemList where account equals to accountId + 1
        defaultBudgetItemShouldNotBeFound("accountId.equals=" + (accountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBudgetItemShouldBeFound(String filter) throws Exception {
        restBudgetItemMockMvc.perform(get("/api/budget-items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budgetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBudgetItemShouldNotBeFound(String filter) throws Exception {
        restBudgetItemMockMvc.perform(get("/api/budget-items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingBudgetItem() throws Exception {
        // Get the budgetItem
        restBudgetItemMockMvc.perform(get("/api/budget-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBudgetItem() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();

        // Update the budgetItem
        BudgetItem updatedBudgetItem = budgetItemRepository.findById(budgetItem.getId()).get();
        // Disconnect from session so that the updates on updatedBudgetItem are not directly saved in db
        em.detach(updatedBudgetItem);
        updatedBudgetItem
            .name(UPDATED_NAME)
            .order(UPDATED_ORDER);
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(updatedBudgetItem);

        restBudgetItemMockMvc.perform(put("/api/budget-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isOk());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
        BudgetItem testBudgetItem = budgetItemList.get(budgetItemList.size() - 1);
        assertThat(testBudgetItem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBudgetItem.getOrder()).isEqualTo(UPDATED_ORDER);
    }

    @Test
    @Transactional
    public void updateNonExistingBudgetItem() throws Exception {
        int databaseSizeBeforeUpdate = budgetItemRepository.findAll().size();

        // Create the BudgetItem
        BudgetItemDTO budgetItemDTO = budgetItemMapper.toDto(budgetItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBudgetItemMockMvc.perform(put("/api/budget-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budgetItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BudgetItem in the database
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBudgetItem() throws Exception {
        // Initialize the database
        budgetItemRepository.saveAndFlush(budgetItem);

        int databaseSizeBeforeDelete = budgetItemRepository.findAll().size();

        // Get the budgetItem
        restBudgetItemMockMvc.perform(delete("/api/budget-items/{id}", budgetItem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BudgetItem> budgetItemList = budgetItemRepository.findAll();
        assertThat(budgetItemList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BudgetItem.class);
        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setId(1L);
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(budgetItem1.getId());
        assertThat(budgetItem1).isEqualTo(budgetItem2);
        budgetItem2.setId(2L);
        assertThat(budgetItem1).isNotEqualTo(budgetItem2);
        budgetItem1.setId(null);
        assertThat(budgetItem1).isNotEqualTo(budgetItem2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BudgetItemDTO.class);
        BudgetItemDTO budgetItemDTO1 = new BudgetItemDTO();
        budgetItemDTO1.setId(1L);
        BudgetItemDTO budgetItemDTO2 = new BudgetItemDTO();
        assertThat(budgetItemDTO1).isNotEqualTo(budgetItemDTO2);
        budgetItemDTO2.setId(budgetItemDTO1.getId());
        assertThat(budgetItemDTO1).isEqualTo(budgetItemDTO2);
        budgetItemDTO2.setId(2L);
        assertThat(budgetItemDTO1).isNotEqualTo(budgetItemDTO2);
        budgetItemDTO1.setId(null);
        assertThat(budgetItemDTO1).isNotEqualTo(budgetItemDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(budgetItemMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(budgetItemMapper.fromId(null)).isNull();
    }
}
