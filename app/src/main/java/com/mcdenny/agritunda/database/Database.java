package com.mcdenny.agritunda.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.mcdenny.agritunda.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "EasyShopDB";
    private static final int DATABASE_VERSION = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //getting the orders already in the cart
    public List<Order> getCart() {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"OrderID","OrderName","Quantity","Price","Discount"};
        String sqlTable = "OrderDetail";

        queryBuilder.setTables(sqlTable);
        Cursor cursor = queryBuilder.query(database,sqlSelect,null,null,null,null,null);

        final List<Order> result = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                result.add(new Order(
                        cursor.getString(cursor.getColumnIndex("OrderID")),
                        cursor.getString(cursor.getColumnIndex("OrderName")),
                        cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount"))
                        ));
            }while (cursor.moveToNext());
        }
        return result;
    }

    //adding orders to the cart
    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(OrderID, OrderName, Quantity, Price, Discount) VALUES ('%s','%s','%s','%s','%s');",
                order.getOrderID(),
                order.getOrderName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount()
        );
        db.execSQL(query);

    }

    //deleting the orders from cart
    public void clearCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }
}

