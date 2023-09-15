package com.n3vers4ydie.foreigncrud.dao;

import com.n3vers4ydie.autosqlcrud.SQLDAO;
import com.n3vers4ydie.foreigncrud.connection.DB;
import com.n3vers4ydie.foreigncrud.model.Product;

public class ProductDAO extends SQLDAO<Product> {

  public ProductDAO() {
    super(DB.getInstance(), "products");
  }
}
