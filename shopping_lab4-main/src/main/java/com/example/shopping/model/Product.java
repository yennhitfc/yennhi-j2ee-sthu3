package com.example.shopping.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private  int id;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Length(min = 0, max = 200 , message = "Tên ảnh không quá 200 ký tự")
    private String image;

    @NotNull(message = "Tên sản phẩm phẩm không được để trống")
    @Min(value = 1 , message = "Giá tiền phải trên 1")
    @Max(value = 999999999, message = "Giá tiền không quá 999999999")
    private long price;

    private Category category;
}
