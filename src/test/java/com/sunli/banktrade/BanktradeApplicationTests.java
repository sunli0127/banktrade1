package com.sunli.banktrade;

import com.sunli.banktrade.dict.TransactionType;
import com.sunli.banktrade.domain.Transaction;
import com.sunli.banktrade.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BanktradeApplicationTests {

	@BeforeEach
	void setUp() {
		// 清理所有交易数据
		List<Transaction> allTransactions = Transaction.findAll();
		for (Transaction t : allTransactions) {
			try {
				Transaction.delete(t.getId());
			} catch (ResourceNotFoundException e) {
				// 忽略删除不存在的交易时的异常
			}
		}
	}

	private Transaction createTestTransaction(String description, BigDecimal amount, TransactionType type, String category) {
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		transaction.setType(type);
		transaction.setCategory(category);
		return transaction;
	}

	@Test
	void testCreateTransaction() {
		// 创建新的交易对象
		Transaction newTransaction = createTestTransaction(
			"测试交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		
		// 创建交易
		Transaction created = Transaction.create(newTransaction);
		
		// 验证创建结果
		assertNotNull(created);
		assertNotNull(created.getId());
		assertEquals("测试交易", created.getDescription());
		assertEquals(new BigDecimal("100.00"), created.getAmount());
		assertEquals(TransactionType.INCOME, created.getType());
		assertEquals("测试类别", created.getCategory());
		assertNotNull(created.getCreatedAt());
		assertNotNull(created.getUpdatedAt());
	}

	@Test
	void testUpdateTransaction() {
		// 先创建交易
		Transaction original = createTestTransaction(
			"原始交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"原始类别"
		);
		Transaction created = Transaction.create(original);
		
		// 修改交易
		Transaction updateData = createTestTransaction(
			"更新后的交易",
			new BigDecimal("200.00"),
			TransactionType.INCOME,
			"原始类别"
		);
		Transaction updated = Transaction.update(created.getId(), updateData);
		
		// 验证更新结果
		assertEquals("更新后的交易", updated.getDescription());
		assertEquals(new BigDecimal("200.00"), updated.getAmount());
	}

	@Test
	void testDeleteTransaction() {
		// 先创建交易
		Transaction transaction = createTestTransaction(
			"待删除交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		Transaction created = Transaction.create(transaction);
		
		// 删除交易
		Transaction.delete(created.getId());
		
		// 验证删除结果
		assertThrows(ResourceNotFoundException.class, () -> {
			Transaction.findById(created.getId());
		});
	}

	@Test
	void testFindById() {
		// 先创建交易
		Transaction transaction = createTestTransaction(
			"查询交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		Transaction created = Transaction.create(transaction);
		
		// 查询交易
		Transaction found = Transaction.findById(created.getId());
		
		// 验证查询结果
		assertNotNull(found);
		assertEquals(created.getId(), found.getId());
		assertEquals(created.getDescription(), found.getDescription());
	}

	@Test
	void testFindAll() {
		// 创建多个交易
		Transaction.create(createTestTransaction(
			"交易1",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"类别1"
		));
		
		Transaction.create(createTestTransaction(
			"交易2",
			new BigDecimal("200.00"),
			TransactionType.EXPENSE,
			"类别2"
		));
		
		// 查询所有交易
		List<Transaction> all = Transaction.findAll();
		
		// 验证查询结果
		assertNotNull(all);
		assertEquals(2, all.size());
	}

	@Test
	void testFindByType() {
		// 创建收入交易
		Transaction.create(createTestTransaction(
			"收入交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"收入类别"
		));
		
		// 创建支出交易
		Transaction.create(createTestTransaction(
			"支出交易",
			new BigDecimal("200.00"),
			TransactionType.EXPENSE,
			"支出类别"
		));
		
		// 查询收入交易
		List<Transaction> incomeTransactions = Transaction.findByType(TransactionType.INCOME);
		
		// 验证查询结果
		assertNotNull(incomeTransactions);
		assertEquals(1, incomeTransactions.size());
		assertTrue(incomeTransactions.stream().allMatch(t -> t.getType() == TransactionType.INCOME));
	}

	@Test
	void testFindByCategory() {
		// 创建交易
		Transaction.create(createTestTransaction(
			"类别1交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"类别1"
		));
		
		// 创建另一个类别的交易
		Transaction.create(createTestTransaction(
			"类别2交易",
			new BigDecimal("200.00"),
			TransactionType.EXPENSE,
			"类别2"
		));
		
		// 查询特定类别的交易
		List<Transaction> categoryTransactions = Transaction.findByCategory("类别1");
		
		// 验证查询结果
		assertNotNull(categoryTransactions);
		assertEquals(1, categoryTransactions.size());
		assertTrue(categoryTransactions.stream().allMatch(t -> t.getCategory().equals("类别1")));
	}

	@Test
	void testDuplicateTransaction() {
		// 创建第一个交易
		Transaction original = createTestTransaction(
			"原始交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		Transaction created = Transaction.create(original);
		assertNotNull(created);

		// 尝试创建完全相同的交易（应该失败）
		Transaction duplicate = createTestTransaction(
			"原始交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		
		assertThrows(IllegalArgumentException.class, () -> {
			Transaction.create(duplicate);
		}, "Should not allow duplicate transaction");

		// 尝试创建描述不同但其他字段相同的交易（应该成功）
		Transaction differentDescription = createTestTransaction(
			"不同的描述",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		Transaction createdDifferentDescription = Transaction.create(differentDescription);
		assertNotNull(createdDifferentDescription);

		// 尝试创建金额不同但其他字段相同的交易（应该成功）
		Transaction differentAmount = createTestTransaction(
			"原始交易",
			new BigDecimal("200.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		Transaction createdDifferentAmount = Transaction.create(differentAmount);
		assertNotNull(createdDifferentAmount);

		// 尝试创建类型不同但其他字段相同的交易（应该成功）
		Transaction differentType = createTestTransaction(
			"原始交易",
			new BigDecimal("100.00"),
			TransactionType.EXPENSE,
			"测试类别"
		);
		Transaction createdDifferentType = Transaction.create(differentType);
		assertNotNull(createdDifferentType);

		// 尝试创建类别不同但其他字段相同的交易（应该成功）
		Transaction differentCategory = createTestTransaction(
			"原始交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"不同类别"
		);
		Transaction createdDifferentCategory = Transaction.create(differentCategory);
		assertNotNull(createdDifferentCategory);

		// 尝试更新为重复交易（应该失败）
		Transaction updateToDuplicate = createTestTransaction(
			"原始交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		assertThrows(IllegalArgumentException.class, () -> {
			Transaction.update(createdDifferentDescription.getId(), updateToDuplicate);
		}, "Should not allow updating to duplicate transaction");

		// 验证所有交易都已正确创建
		List<Transaction> allTransactions = Transaction.findAll();
		assertEquals(5, allTransactions.size(), "Should have exactly 5 unique transactions");
	}

	@Test
	void testInvalidTransaction() {
		// 测试空描述
		Transaction nullDescription = createTestTransaction(
			null,
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		assertThrows(IllegalArgumentException.class, () -> {
			Transaction.create(nullDescription);
		});

		// 测试负数金额
		Transaction negativeAmount = createTestTransaction(
			"测试交易",
			new BigDecimal("-100.00"),
			TransactionType.INCOME,
			"测试类别"
		);
		assertThrows(IllegalArgumentException.class, () -> {
			Transaction.create(negativeAmount);
		});

		// 测试空类别
		Transaction nullCategory = createTestTransaction(
			"测试交易",
			new BigDecimal("100.00"),
			TransactionType.INCOME,
			null
		);
		assertThrows(IllegalArgumentException.class, () -> {
			Transaction.create(nullCategory);
		});
	}
}
