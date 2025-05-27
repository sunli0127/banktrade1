# 交易管理 API 测试命令

## 1. 创建交易

```bash
# 创建收入交易
curl -X POST http://localhost:8080/api/transactions \
    -H "Content-Type: application/json" \
    -d '{
        "description": "工资",
        "amount": 5000.00,
        "type": "INCOME",
        "category": "工资"
    }'

# 创建支出交易
curl -X POST http://localhost:8080/api/transactions \
    -H "Content-Type: application/json" \
    -d '{
        "description": "购物",
        "amount": 100.00,
        "type": "EXPENSE",
        "category": "日常消费"
    }'
```

## 2. 获取单个交易

```bash
# 替换 {id} 为实际的交易ID
curl -X GET http://localhost:8080/api/transactions/{id}
```

## 3. 获取所有交易（分页）

```bash
# 获取第一页，每页10条
curl -X GET "http://localhost:8080/api/transactions?page=0&size=10"

# 获取第二页，每页20条
curl -X GET "http://localhost:8080/api/transactions?page=1&size=20"
```

## 4. 按类型查询交易

```bash
# 查询收入交易
curl -X GET "http://localhost:8080/api/transactions/type/INCOME?page=0&size=10"

# 查询支出交易
curl -X GET "http://localhost:8080/api/transactions/type/EXPENSE?page=0&size=10"
```

## 5. 按类别查询交易

```bash
# 替换 {category} 为实际的类别名称
curl -X GET "http://localhost:8080/api/transactions/category/{category}?page=0&size=10"
```

## 6. 更新交易

```bash
# 替换 {id} 为实际的交易ID
curl -X PUT http://localhost:8080/api/transactions/{id} \
    -H "Content-Type: application/json" \
    -d '{
        "description": "更新后的描述",
        "amount": 6000.00,
        "type": "INCOME",
        "category": "工资"
    }'
```

## 7. 删除交易

```bash
# 替换 {id} 为实际的交易ID
curl -X DELETE http://localhost:8080/api/transactions/{id}
```

## 注意事项

1. 所有请求的 `type` 字段必须是 "INCOME" 或 "EXPENSE"
2. `amount` 必须是正数
3. 所有字段都是必填的
4. 分页参数 `page` 从 0 开始计数
5. 如果需要在 Windows 命令行中使用，需要将单引号 `'` 改为双引号 `"`，并转义内部的双引号 