package com.sunli.banktrade.controller;

import com.sunli.banktrade.dict.TransactionType;
import com.sunli.banktrade.domain.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST控制器，处理所有与交易相关的HTTP请求
 * 提供完整的CRUD操作以及按类型和类别的查询功能
 *
 * @author sunli
 * @version 1.0
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    /**
     * 创建新的交易记录
     *
     * @param transaction 包含交易详情的请求体
     * @return 返回创建成功的交易记录，HTTP状态码为201
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return new ResponseEntity<>(Transaction.create(transaction), HttpStatus.CREATED);
    }

    /**
     * 更新指定ID的交易记录
     *
     * @param id 要更新的交易ID
     * @param transaction 包含更新信息的请求体
     * @return 返回更新后的交易记录
     * @throws ResourceNotFoundException 当指定ID的交易不存在时抛出
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return ResponseEntity.ok(Transaction.update(id, transaction));
    }

    /**
     * 删除指定ID的交易记录
     *
     * @param id 要删除的交易ID
     * @return 返回204 No Content状态码表示删除成功
     * @throws ResourceNotFoundException 当指定ID的交易不存在时抛出
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Transaction.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取指定ID的交易记录
     *
     * @param id 要查询的交易ID
     * @return 返回查询到的交易记录
     * @throws ResourceNotFoundException 当指定ID的交易不存在时抛出
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(Transaction.findById(id));
    }

    /**
     * 分页获取所有交易记录
     *
     * @param pageable 分页参数，包含页码和每页大小
     * @return 返回分页后的交易记录列表
     */
    @GetMapping
    public ResponseEntity<Page<Transaction>> getAllTransactions(Pageable pageable) {
        List<Transaction> transactions = Transaction.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());
        
        return ResponseEntity.ok(new PageImpl<>(
            transactions.subList(start, end),
            pageable,
            transactions.size()
        ));
    }

    /**
     * 按交易类型分页查询交易记录
     *
     * @param type 交易类型（INCOME/EXPENSE）
     * @param pageable 分页参数
     * @return 返回符合条件的交易记录分页列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Transaction>> getTransactionsByType(
            @PathVariable TransactionType type,
            Pageable pageable) {
        List<Transaction> transactions = Transaction.findByType(type);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());
        
        return ResponseEntity.ok(new PageImpl<>(
            transactions.subList(start, end),
            pageable,
            transactions.size()
        ));
    }

    /**
     * 按交易类别分页查询交易记录
     *
     * @param category 交易类别
     * @param pageable 分页参数
     * @return 返回符合条件的交易记录分页列表
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Transaction>> getTransactionsByCategory(
            @PathVariable String category,
            Pageable pageable) {
        List<Transaction> transactions = Transaction.findByCategory(category);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());
        
        return ResponseEntity.ok(new PageImpl<>(
            transactions.subList(start, end),
            pageable,
            transactions.size()
        ));
    }
} 