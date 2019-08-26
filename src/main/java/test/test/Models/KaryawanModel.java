/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.test.Models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

/**
 *
 * @author user
 */
@Table("karyawan")
@BelongsTo(parent = JabatanModel.class, foreignKeyName = "id_jabatan")
public class KaryawanModel extends Model {}