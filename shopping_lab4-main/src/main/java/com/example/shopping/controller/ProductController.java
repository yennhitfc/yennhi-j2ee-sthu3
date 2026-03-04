package com.example.shopping.controller;

import com.example.shopping.model.Category;
import com.example.shopping.model.Product;
import com.example.shopping.service.CategoryService;
import com.example.shopping.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @Value("${upload.path}")
    private String uploadDir;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.findAll();
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/products";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setCategory(new Category());
        model.addAttribute("product", product);
        return "product/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product") Product product,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(value = "imageFile", required = false)
                         MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            return "product/create";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Tạo thư mục nếu chưa tồn tại
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                product.setImage(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo sản phẩm thành công");
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
                    return "redirect:/products";
                });
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable int id,
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {

        if (bindingResult.hasErrors()) {
            return "product/edit";
        }

        Product oldProduct = productService.findById(id).orElseThrow();

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);

                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Files.copy(imageFile.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                product.setImage(fileName);
            } else {
                product.setImage(oldProduct.getImage());
            }
        } catch (Exception e) {
            bindingResult.reject("image", "Upload ảnh thất bại");
            return "product/edit";
        }

        product.setId(id);
        productService.save(product);
        return "redirect:/products";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (productService.findById(id).isPresent()) {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm");
        }
        return "redirect:/products";
    }
}