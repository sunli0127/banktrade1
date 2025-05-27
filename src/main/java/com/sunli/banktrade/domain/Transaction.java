package com.sunli.banktrade.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sunli.banktrade.dict.TransactionType;
import com.sunli.banktrade.exception.ResourceNotFoundException;

/**
 * 交易实体类，表示系统中的交易记录
 * 使用内存存储实现，支持并发操作
 * 
 *  我特意写了一个充血模型将模型的功能内聚在了模型内部，是因为充血模型与DDD更为适配。事实上，充血模型和贫血模型各有优劣，要根据实际情况选择
 *
 * @author sunli
 * @version 1.0
 */
public class Transaction {
    /** 使用线程安全的Map存储所有交易记录 */
    private static final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();
    /** 用于生成自增ID的计数器 */
    private static long nextId = 1;
    
    private Long id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 默认构造函数，初始化创建时间和更新时间
     */
    public Transaction() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 验证交易数据的有效性
     *
     * @param transaction 要验证的交易对象
     * @throws IllegalArgumentException 当数据无效时抛出
     */
    private static void validateTransaction(Transaction transaction) {
        if (transaction.description == null || transaction.description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (transaction.amount == null || transaction.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (transaction.type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (transaction.category == null || transaction.category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
    }
    
    /**
     * 检查是否存在重复交易（因为是demo，我假定关键参数相同即是重复交易，实际业务中可能还会根据外部订单号、令牌、时间戳等其它参数共同判定）
     *
     * @param transaction 要检查的交易对象
     * @throws IllegalArgumentException 当发现重复交易时抛出
     */
    private static void checkDuplicateTransaction(Transaction transaction) {
        // 获取所有其他交易（排除当前交易）
        List<Transaction> otherTransactions = transactions.values().stream()
            .filter(t -> transaction.getId() == null || !t.getId().equals(transaction.getId()))
            .collect(Collectors.toList());
            
        // 检查是否存在重复
        boolean isDuplicate = otherTransactions.stream()
            .anyMatch(t -> 
                t.getDescription().equals(transaction.getDescription()) &&
                t.getAmount().compareTo(transaction.getAmount()) == 0 &&
                t.getType() == transaction.getType() &&
                t.getCategory().equals(transaction.getCategory())
            );
            
        if (isDuplicate) {
            throw new IllegalArgumentException("Duplicate transaction found with same description, amount, type and category");
        }
    }
    
    /**
     * 创建新的交易记录
     *
     * @param transaction 要创建的交易对象
     * @return 创建成功后的交易对象（包含ID）
     * @throws IllegalArgumentException 当必填字段为空、金额小于等于0或存在重复交易时抛出
     */
    public static Transaction create(Transaction transaction) {
        validateTransaction(transaction);
        checkDuplicateTransaction(transaction);
        
        transaction.id = nextId++;
        transaction.createdAt = LocalDateTime.now();
        transaction.updatedAt = LocalDateTime.now();
        transactions.put(transaction.id, transaction);
        return transaction;
    }
    
    /**
     * 更新指定ID的交易记录
     *
     * @param id 要更新的交易ID
     * @param transaction 包含更新信息的交易对象
     * @return 更新后的交易对象
     * @throws ResourceNotFoundException 当指定ID的交易不存在时抛出
     * @throws IllegalArgumentException 当更新数据无效或存在重复交易时抛出
     */
    public static Transaction update(Long id, Transaction transaction) {
        Transaction existing = transactions.get(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        
        validateTransaction(transaction);
        transaction.id = id; // 设置ID后再检查重复
        checkDuplicateTransaction(transaction);
        
        transaction.createdAt = existing.createdAt;
        transaction.updatedAt = LocalDateTime.now();
        transactions.put(id, transaction);
        return transaction;
    }
    
    /**
     * 删除指定ID的交易记录
     *
     * @param id 要删除的交易ID
     * @throws ResourceNotFoundException 当指定ID的交易不存在时抛出
     */
    public static void delete(Long id) {
        if (!transactions.containsKey(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactions.remove(id);
    }
    
    /**
     * 根据ID查找交易记录
     *
     * @param id 要查询的交易ID
     * @return 查询到的交易对象
     * @throws ResourceNotFoundException 当指定ID的交易不存在时抛出
     */
    public static Transaction findById(Long id) {
        Transaction transaction = transactions.get(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        return transaction;
    }
    
    /**
     * 获取所有交易记录
     *
     * @return 所有交易记录的列表
     */
    public static List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
    
    /**
     * 根据交易类型查询交易记录
     *
     * @param type 交易类型（INCOME/EXPENSE）
     * @return 符合条件的交易记录列表
     */
    public static List<Transaction> findByType(TransactionType type) {
        return transactions.values().stream()
                .filter(t -> t.type == type)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据交易类别查询交易记录
     *
     * @param category 交易类别
     * @return 符合条件的交易记录列表
     */
    public static List<Transaction> findByCategory(String category) {
        return transactions.values().stream()
                .filter(t -> t.category.equals(category))
                .collect(Collectors.toList());
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    /**
     * 通过字符串设置交易类型
     *
     * @param typeStr 交易类型字符串（"INCOME"或"EXPENSE"）
     * @throws IllegalArgumentException 当类型字符串无效时抛出
     */
    public void setType(String typeStr) {
        try {
            this.type = TransactionType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + typeStr);
        }
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 