package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.InstructorFileCreateDTO;
import uz.uzkassa.smartpos.dto.InstructorFileDTO;
import uz.uzkassa.smartpos.dto.InstructorFileUpdateDTO;
import uz.uzkassa.smartpos.enums.InstructorType;
import uz.uzkassa.smartpos.filter.InstructorFilter;
import uz.uzkassa.smartpos.service.InstructorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructor/file")
@RequiredArgsConstructor
public class InstructorResource {
    private final InstructorService instructorService;

    @PostMapping
    public InstructorFileDTO create(@RequestBody InstructorFileCreateDTO dto) {
        return instructorService.create(dto);
    }

    @PutMapping
    public InstructorFileDTO update(@RequestBody InstructorFileUpdateDTO dto) {
        return instructorService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void update(@PathVariable Long id) {
        instructorService.delete(id);
    }

    @GetMapping("/by-type")
    public List<InstructorFileDTO> findAllFileByInstructorType(@RequestParam InstructorType type) {
        return instructorService.findAllFileByInstructorType(type);
    }

    @GetMapping
    public Page<InstructorFileDTO> getAllFiles(@ParameterObject InstructorFilter filter) {
        return instructorService.findAllFile(filter);
    }

    @GetMapping("/types")
    public InstructorType[] instructorTypes() {
        return InstructorType.values();
    }

}
