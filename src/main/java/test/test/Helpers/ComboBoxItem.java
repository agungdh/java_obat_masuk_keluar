/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.test.Helpers;

/**
 *
 * @author user
 */
public class ComboBoxItem {
    private String id;
    private String description;

    public ComboBoxItem(String id, String description)
    {
        this.id = id;
        this.description = description;
    }

    public String getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }
    
    @Override
    public String toString()
    {
        return description;
    }
}
