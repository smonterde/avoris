package com.payroll.employee;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
	
	private final EmployeeRepository repository;
	
	private final EmployeeModelAssembler assembler;
	
	public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}
	
	@GetMapping("/employees")
	public CollectionModel<EntityModel<Employee>> all() {
		
		List<EntityModel<Employee>> employees = repository.findAll().stream()
			    .map(assembler::toModel)
			    .collect(Collectors.toList());
		
		return new CollectionModel<>(employees, 
				linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	} 
	
	@PostMapping("/employees")
	public Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);		
	}
	
	@GetMapping("/employees/{id}")
	public EntityModel<Employee> one(@PathVariable Long id) {
		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
		
		return assembler.toModel(employee);
		
	}
	
	@PutMapping("/employees/{id}")
	public Employee replaceEmployee(@RequestBody  Employee newEmployee, @PathVariable Long id) {
		
		return repository.findById(id)
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					return repository.save(employee);
				})
				.orElseGet(() -> {
					newEmployee.setId(id);
					return repository.save(newEmployee);
				});
	}
	
	@DeleteMapping("/employees/{id}")
	public void deleteEmployee(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
