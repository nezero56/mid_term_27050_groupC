# QUICK REFERENCE: Creating REST APIs in Spring Boot

## The 5-Layer Architecture

```
Request → Controller → Service → Repository → Database
         (REST API)   (Logic)   (Queries)    (PostgreSQL)
```

---

## Step-by-Step Checklist

### ✅ Step 1: Create Entity (Model)
```java
@Entity
@Table(name = "table_name")
@Data
public class MyEntity {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private String name;
}
```

### ✅ Step 2: Create Repository
```java
@Repository
public interface MyRepository extends JpaRepository<MyEntity, Long> {
    Optional<MyEntity> findByName(String name);
    boolean existsByName(String name);
}
```

### ✅ Step 3: Create Service
```java
@Service
@Transactional
public class MyService {
    @Autowired
    private MyRepository repository;
    
    public MyEntity create(MyEntity entity) {
        return repository.save(entity);
    }
    
    public List<MyEntity> getAll() {
        return repository.findAll();
    }
}
```

### ✅ Step 4: Create Controller
```java
@RestController
@RequestMapping("/api/myentity")
public class MyController {
    @Autowired
    private MyService service;
    
    @PostMapping
    public ResponseEntity<MyEntity> create(@RequestBody MyEntity entity) {
        return new ResponseEntity<>(service.create(entity), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<MyEntity>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
```

### ✅ Step 5: Test in Postman
- Start application: `mvn spring-boot:run`
- Open Postman
- Create request
- Test!

---

## Common Annotations

### Entity Annotations
| Annotation | Purpose |
|------------|---------|
| `@Entity` | Marks class as database table |
| `@Table(name="...")` | Specifies table name |
| `@Id` | Primary key |
| `@GeneratedValue` | Auto-generate ID |
| `@Column` | Column properties |
| `@ManyToOne` | Many-to-one relationship |
| `@OneToMany` | One-to-many relationship |
| `@ManyToMany` | Many-to-many relationship |
| `@OneToOne` | One-to-one relationship |

### Controller Annotations
| Annotation | Purpose |
|------------|---------|
| `@RestController` | REST API controller |
| `@RequestMapping` | Base URL |
| `@GetMapping` | Handle GET requests |
| `@PostMapping` | Handle POST requests |
| `@PutMapping` | Handle PUT requests |
| `@DeleteMapping` | Handle DELETE requests |
| `@PathVariable` | Extract from URL path |
| `@RequestParam` | Extract from query string |
| `@RequestBody` | Convert JSON to object |

### Service Annotations
| Annotation | Purpose |
|------------|---------|
| `@Service` | Service component |
| `@Transactional` | Database transaction |
| `@Autowired` | Dependency injection |

---

## HTTP Methods & Status Codes

### HTTP Methods
| Method | Purpose | Example |
|--------|---------|---------|
| GET | Retrieve data | Get all users |
| POST | Create new | Create user |
| PUT | Update existing | Update user |
| DELETE | Delete | Delete user |

### Status Codes
| Code | Meaning | When to Use |
|------|---------|-------------|
| 200 | OK | Successful GET/PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid input |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Internal error |

---

## Repository Method Naming

```java
// Find by field
findByName(String name)
findByEmail(String email)

// Find with conditions
findByNameAndEmail(String name, String email)
findByNameOrEmail(String name, String email)

// Check existence
existsByEmail(String email)

// Count
countByStatus(String status)

// Delete
deleteByEmail(String email)

// Sorting
findByNameOrderByCreatedAtDesc(String name)

// Null checks
findByParentIsNull()
findByParentIsNotNull()

// Like queries
findByNameContaining(String keyword)
findByNameStartingWith(String prefix)
```

---

## Common Patterns

### Create (POST)
```java
@PostMapping
public ResponseEntity<Entity> create(@RequestBody Entity entity) {
    Entity saved = service.save(entity);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}
```

### Read All (GET)
```java
@GetMapping
public ResponseEntity<List<Entity>> getAll() {
    return ResponseEntity.ok(service.getAll());
}
```

### Read One (GET)
```java
@GetMapping("/{id}")
public ResponseEntity<Entity> getById(@PathVariable Long id) {
    return service.getById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

### Update (PUT)
```java
@PutMapping("/{id}")
public ResponseEntity<Entity> update(@PathVariable Long id, @RequestBody Entity entity) {
    Entity updated = service.update(id, entity);
    return ResponseEntity.ok(updated);
}
```

### Delete (DELETE)
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

---

## Pagination Example

### Repository
```java
Page<User> findAll(Pageable pageable);
```

### Service
```java
public Page<User> getAll(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return repository.findAll(pageable);
}
```

### Controller
```java
@GetMapping
public ResponseEntity<Map<String, Object>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    Page<User> userPage = service.getAll(page, size, "name");
    
    Map<String, Object> response = new HashMap<>();
    response.put("data", userPage.getContent());
    response.put("currentPage", userPage.getNumber());
    response.put("totalItems", userPage.getTotalElements());
    response.put("totalPages", userPage.getTotalPages());
    
    return ResponseEntity.ok(response);
}
```

---

## Testing Checklist

- [ ] Entity created with proper annotations
- [ ] Repository interface created
- [ ] Service class created with business logic
- [ ] Controller created with REST endpoints
- [ ] Application runs without errors
- [ ] Postman collection created
- [ ] All APIs tested
- [ ] Database verified

---

## Common Errors & Solutions

### Error: Port 8080 already in use
**Solution:** Kill the process
```bash
netstat -ano | findstr :8080
taskkill /F /PID <process_id>
```

### Error: Cannot find symbol @Entity
**Solution:** Add dependency in pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### Error: No qualifying bean
**Solution:** Add @Autowired or check component scanning

### Error: JSON parse error
**Solution:** Check JSON format in Postman body

---

## Resources

- Spring Boot Docs: https://spring.io/projects/spring-boot
- JPA Docs: https://spring.io/projects/spring-data-jpa
- Postman: https://www.postman.com/

---

**You now know how to create REST APIs in Spring Boot!** 🎉
