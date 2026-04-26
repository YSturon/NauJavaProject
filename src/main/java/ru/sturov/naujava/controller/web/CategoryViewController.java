package ru.sturov.naujava.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sturov.naujava.repository.CategoryRepository;

/** WEB-контроллер для отображения списка категорий в HTML-шаблоне. */
@Controller
@RequestMapping("/view/categories")
public class CategoryViewController {

    private final CategoryRepository categoryRepository;

    public CategoryViewController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    /**
     * Формирует модель страницы со списком категорий.
     *
     * @param model модель представления
     * @return имя thymeleaf-шаблона
     */
    @GetMapping("/list")
    public String categoryListView(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "category-list";
    }
}
