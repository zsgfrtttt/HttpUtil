package com.csz.app.base.http;


/**
 * @author caishuzhan
 */
public class Person {
    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" + "age=" + age + ", name='" + name + '\'' + '}';
    }
}
