package com.n3vers4ydie.foreigncrud.model;

import com.n3vers4ydie.autosqlcrud.SQLModel;
import java.math.BigDecimal;

public class Product extends SQLModel {

  private String name;
  private BigDecimal price;
  private String categoryId;

  public Product(String id, String name, BigDecimal price, String categoryId) {
    super(id);
    setName(name);
    setPrice(price);
    setCategoryId(categoryId);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    values.put("name", name);
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
    values.put("price", price);
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
    values.put("category_id", categoryId);
  }
}
