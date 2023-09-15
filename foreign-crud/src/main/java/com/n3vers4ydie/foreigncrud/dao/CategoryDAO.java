package com.n3vers4ydie.foreigncrud.dao;

import com.n3vers4ydie.autosqlcrud.SQLDAO;
import com.n3vers4ydie.foreigncrud.connection.DB;
import com.n3vers4ydie.foreigncrud.model.Category;

public class CategoryDAO extends SQLDAO<Category> {

  public CategoryDAO() {
    super(DB.getInstance(), "categories");
  }
}
