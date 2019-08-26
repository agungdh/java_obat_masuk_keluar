/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.test.Forms;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import test.test.Models.JabatanModel;

import test.test.Models.GajiModel;
import test.test.Models.KaryawanModel;
import test.test.Reports.Config;

/**
 *
 * @author user
 */
public class Karyawan extends javax.swing.JFrame {
    private List<Integer> comboJabatanID = new ArrayList<Integer>();
    private int comboJabatanIndex;
    private int selectedComboJabatanIndex;
    
    private DefaultTableModel model = new DefaultTableModel();
    private String ID;
    private String state;
    /**
     * Creates new form PangkatGol
     */
    public Karyawan() {
        initComponents();
                
        loadTable();
                
        TextCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                cari();
            }

            public void removeUpdate(DocumentEvent e) {
                cari();
            }

            public void changedUpdate(DocumentEvent e) {
                cari();
            }
        });
        
        loadComboBox();
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    public void loadComboBox() {
        Jabatan.removeAllItems();
        Base.open();
        LazyList<JabatanModel> jabatans = JabatanModel.findAll();
        
        for(JabatanModel jabatan : jabatans) {
            comboJabatanID.add(Integer.parseInt(jabatan.getString("id")));
            Jabatan.addItem(jabatan.getString("jabatan"));
        }

        Base.close();
    }
    
    public void cari() {
        if (TextCari.getText().equals("")) {
            loadTable();
        } else {
            loadTable(TextCari.getText());
        }
    }
    
    private void loadTableHelper(LazyList<KaryawanModel> karyawans) {
        model = new DefaultTableModel();
                
        model.addColumn("#ID");
        model.addColumn("Nama");
        model.addColumn("NIK");
        model.addColumn("Jenis Kelamin");
        model.addColumn("Agama");
        model.addColumn("Tempat Lahir");
        model.addColumn("Tanggal Lahir");
        model.addColumn("Pendidikan");
        model.addColumn("Pekerjaan");
        model.addColumn("Status Kawin");
        
        Base.open();
        
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            
            for(KaryawanModel karyawan : karyawans) {                
                Date tanggal = format.parse(karyawan.getString("tanggal_lahir"));

                SimpleDateFormat parsedFormat = new SimpleDateFormat("dd-MM-YYYY");
                String parsedtanggal = parsedFormat.format(tanggal);
                
                model.addRow(new Object[]{
                    karyawan.getId(),
                    karyawan.getString("nama"),
                    karyawan.getString("nik"),
                    karyawan.getString("kelamin"),
                    karyawan.getString("agama"),
                    karyawan.getString("tempat_lahir"),
                    parsedtanggal,
                    karyawan.getString("pendidikan"),
                    karyawan.getString("pekerjaan"),
                    karyawan.getString("status_kawin"),
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        Base.close();
        
        TablePegawai.setModel(model);
        
        setState("index");
    }
    
    private void formatTextInteger() {
       
    }
    
    private void loadTable() {
        Base.open();
        LazyList<KaryawanModel> karyawan = KaryawanModel.findAll();
        Base.close();
        
        loadTableHelper(karyawan);
    }

    private void loadTable(String cari) {
        Base.open();
        LazyList<KaryawanModel> karyawan = KaryawanModel.where("nama like ? or nik like ? or kelamin like ?", '%' + cari + '%', '%' + cari + '%', '%' + cari + '%');
        Base.close();
        
        loadTableHelper(karyawan);
    }

    
    private void hapusData() {
        Base.open();
        KaryawanModel karyawan = KaryawanModel.findById(ID);
        try {
            karyawan.delete();
        } catch (DBException e) {
            JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        }
        Base.close();
    }
    
    private void setState(String IndexOrEdit) {
        if (IndexOrEdit.equals("index")) {
            ButtonTambahUbah.setText("Tambah");
            ButtonResetHapus.setText("Reset");
            
            state = IndexOrEdit;
        } else if (IndexOrEdit.equals("edit")) {
            ButtonTambahUbah.setText("Ubah");
            ButtonResetHapus.setText("Hapus");
            
            state = IndexOrEdit;
        } else {
            JOptionPane.showMessageDialog(null, "Index/Edit ?");
        }
    }
    
    private void tambahData() {
        Base.open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            KaryawanModel karyawan = new KaryawanModel();
            karyawan.set("nama", Nama.getText());
            karyawan.set("nik", NIK.getText());
            if (laki.isSelected()) {
                karyawan.set("kelamin", "Laki-Laki");   
            } else {
                karyawan.set("kelamin", "Perempuan");
            }
            karyawan.set("tempat_lahir", Tempat.getText());
            karyawan.set("tanggal_lahir", dateFormat.format(TanggalLahir.getDate()));
            karyawan.set("agama", Agama.getText());
            karyawan.set("pendidikan", Pendidikan.getText());
            karyawan.set("pekerjaan", Pekerjaan.getText());
            karyawan.set("status_kawin", Kawin.getText());
            karyawan.set("id_jabatan", selectedComboJabatanIndex);
            karyawan.save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        Base.close();
    }
    
    private void ubahData() {
        Base.open();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            KaryawanModel karyawan = KaryawanModel.findById(ID);
            karyawan.set("nama", Nama.getText());
            karyawan.set("nik", NIK.getText());
            if (laki.isSelected()) {
                karyawan.set("kelamin", "Laki-Laki");   
            } else {
                karyawan.set("kelamin", "Perempuan");
            }
            karyawan.set("tempat_lahir", Tempat.getText());
            karyawan.set("tanggal_lahir", dateFormat.format(TanggalLahir.getDate()));
            karyawan.set("agama", Agama.getText());
            karyawan.set("pendidikan", Pendidikan.getText());
            karyawan.set("pekerjaan", Pekerjaan.getText());
            karyawan.set("status_kawin", Kawin.getText());
            karyawan.set("id_jabatan", selectedComboJabatanIndex);
            karyawan.save();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        Base.close();
    }

    private void resetForm() {
        Nama.setText("");
        NIK.setText("");
        Tempat.setText("");
        Agama.setText("");
        Pendidikan.setText("");
        Pekerjaan.setText("");
        Kawin.setText("");
        TanggalLahir.setDate(null);
        laki.setSelected(false);
        perempuan.setSelected(false);
        Jabatan.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ButtonRefresh = new javax.swing.JButton();
        ScrollPane = new javax.swing.JScrollPane();
        TablePegawai = new javax.swing.JTable();
        ButtonTambahUbah = new javax.swing.JButton();
        ButtonResetHapus = new javax.swing.JButton();
        TextCari = new javax.swing.JTextField();
        LabelCari = new javax.swing.JLabel();
        Nama = new javax.swing.JTextField();
        LabelCari1 = new javax.swing.JLabel();
        LabelCari2 = new javax.swing.JLabel();
        NIK = new javax.swing.JTextField();
        LabelCari3 = new javax.swing.JLabel();
        LabelCari4 = new javax.swing.JLabel();
        LabelCari5 = new javax.swing.JLabel();
        LabelCari6 = new javax.swing.JLabel();
        LabelCari7 = new javax.swing.JLabel();
        LabelCari8 = new javax.swing.JLabel();
        TanggalLahir = new com.toedter.calendar.JDateChooser();
        laki = new javax.swing.JRadioButton();
        perempuan = new javax.swing.JRadioButton();
        Agama = new javax.swing.JTextField();
        Pendidikan = new javax.swing.JTextField();
        Pekerjaan = new javax.swing.JTextField();
        Kawin = new javax.swing.JTextField();
        Tempat = new javax.swing.JTextField();
        LabelCari9 = new javax.swing.JLabel();
        Jabatan = new javax.swing.JComboBox<>();
        LabelCari10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Karyawan");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ButtonRefresh.setText("Refresh");
        ButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonRefreshActionPerformed(evt);
            }
        });

        TablePegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        TablePegawai.getTableHeader().setReorderingAllowed(false);
        TablePegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablePegawaiMouseClicked(evt);
            }
        });
        ScrollPane.setViewportView(TablePegawai);

        ButtonTambahUbah.setText("Tambah");
        ButtonTambahUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonTambahUbahActionPerformed(evt);
            }
        });

        ButtonResetHapus.setText("Reset");
        ButtonResetHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonResetHapusActionPerformed(evt);
            }
        });

        TextCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextCariActionPerformed(evt);
            }
        });

        LabelCari.setText("Cari (NIK / Nama / Jenis Kelamin)");

        Nama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NamaActionPerformed(evt);
            }
        });

        LabelCari1.setText("Nama");

        LabelCari2.setText("NIK");

        NIK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NIKActionPerformed(evt);
            }
        });

        LabelCari3.setText("Jenis Kelamin");

        LabelCari4.setText("Tanggal Lahir");

        LabelCari5.setText("Agama");

        LabelCari6.setText("Pendidikan");

        LabelCari7.setText("Pekerjaan");

        LabelCari8.setText("Status Kawin");

        TanggalLahir.setDateFormatString("dd-MM-yyyy");

        laki.setText("Laki-Laki");
        laki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lakiActionPerformed(evt);
            }
        });

        perempuan.setText("Perempuan");
        perempuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perempuanActionPerformed(evt);
            }
        });

        Agama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgamaActionPerformed(evt);
            }
        });

        Pendidikan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PendidikanActionPerformed(evt);
            }
        });

        Pekerjaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PekerjaanActionPerformed(evt);
            }
        });

        Kawin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KawinActionPerformed(evt);
            }
        });

        Tempat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TempatActionPerformed(evt);
            }
        });

        LabelCari9.setText("Tempat Lahir");

        Jabatan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        Jabatan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JabatanItemStateChanged(evt);
            }
        });
        Jabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JabatanActionPerformed(evt);
            }
        });

        LabelCari10.setText("Jabatan");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ScrollPane)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelCari7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelCari6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelCari5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(LabelCari, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(28, 28, 28)
                            .addComponent(TextCari, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(LabelCari1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Nama, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(LabelCari2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LabelCari4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LabelCari3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LabelCari8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LabelCari9, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LabelCari10, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(laki)
                                    .addGap(51, 51, 51)
                                    .addComponent(perempuan))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(NIK, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addComponent(TanggalLahir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Agama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addComponent(Pendidikan, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addComponent(Pekerjaan, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addComponent(Kawin, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addComponent(Tempat, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addComponent(Jabatan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(92, 92, 92))
            .addGroup(layout.createSequentialGroup()
                .addGap(185, 185, 185)
                .addComponent(ButtonTambahUbah)
                .addGap(29, 29, 29)
                .addComponent(ButtonRefresh)
                .addGap(28, 28, 28)
                .addComponent(ButtonResetHapus)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelCari1)
                    .addComponent(Nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelCari2)
                    .addComponent(NIK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelCari3)
                        .addComponent(laki))
                    .addComponent(perempuan, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Tempat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelCari9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LabelCari4)
                    .addComponent(TanggalLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelCari5)
                    .addComponent(Agama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelCari6)
                    .addComponent(Pendidikan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelCari7)
                    .addComponent(Pekerjaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelCari8)
                    .addComponent(Kawin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelCari10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonRefresh)
                    .addComponent(ButtonTambahUbah)
                    .addComponent(ButtonResetHapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TextCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelCari))
                .addGap(18, 18, 18)
                .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonRefreshActionPerformed
        resetForm();
        loadTable();
        TextCari.setText("");
    }//GEN-LAST:event_ButtonRefreshActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        new MenuUtama().setVisible(true);
        
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void TablePegawaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablePegawaiMouseClicked
        int i =TablePegawai.getSelectedRow();
        if(i>=0){
            ID = model.getValueAt(i, 0).toString();

            Base.open();
            KaryawanModel karyawan = KaryawanModel.findById(ID);
            Base.close();

            Nama.setText(karyawan.getString("nama"));
            NIK.setText(karyawan.getString("nik"));
            Tempat.setText(karyawan.getString("tempat_lahir"));
            Agama.setText(karyawan.getString("agama"));
            Pendidikan.setText(karyawan.getString("pendidikan"));
            Pekerjaan.setText(karyawan.getString("pekerjaan"));
            Kawin.setText(karyawan.getString("status_kawin"));
            Jabatan.setSelectedIndex(comboJabatanID.indexOf(Integer.parseInt(karyawan.getString("id_jabatan"))));
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            
            try {
                TanggalLahir.setDate(format.parse(karyawan.getString("tanggal_lahir")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            
            if (karyawan.getString("kelamin").equals("Laki-Laki")) {
                laki.setSelected(true);
                perempuan.setSelected(false); 
            } else {
                perempuan.setSelected(true); 
                laki.setSelected(false);
            }
            
            setState("edit");
        }
    }//GEN-LAST:event_TablePegawaiMouseClicked

    private void ButtonTambahUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonTambahUbahActionPerformed
        if (state.equals("index")) {
            if (Nama.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Nama Masih Kosong !!!");
            } else if (NIK.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form NIK Masih Kosong !!!");
            } else if (Tempat.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Tempat Lahir Masih Kosong !!!");
            } else if (Agama.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Agama Masih Kosong !!!");
            } else if (Pendidikan.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Pendidikan Masih Kosong !!!");
            } else if (Pekerjaan.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Pekerjaan Masih Kosong !!!");
            } else if (Kawin.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Status Kawin Masih Kosong !!!");
            } else if (!laki.isSelected() && !perempuan.isSelected()) {
                JOptionPane.showMessageDialog(null, "Form Jenis Kelamin Belum Dipilih !!!");
            } else if (TanggalLahir.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Form Tanggal Lahir Masih Kosong !!!");
            } else {
                tambahData();
                resetForm();
                loadTable();
            }
        } else {
            if (Nama.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Nama Masih Kosong !!!");
            } else if (NIK.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form NIK Masih Kosong !!!");
            } else if (Tempat.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Tempat Lahir Masih Kosong !!!");
            } else if (Agama.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Agama Masih Kosong !!!");
            } else if (Pendidikan.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Pendidikan Masih Kosong !!!");
            } else if (Pekerjaan.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Pekerjaan Masih Kosong !!!");
            } else if (Kawin.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Form Status Kawin Masih Kosong !!!");
            } else if (!laki.isSelected() && !perempuan.isSelected()) {
                JOptionPane.showMessageDialog(null, "Form Jenis Kelamin Belum Dipilih !!!");
            } else if (TanggalLahir.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Form Tanggal Lahir Masih Kosong !!!");
            } else {
                ubahData();
                resetForm();
                loadTable();
            }
        }
    }//GEN-LAST:event_ButtonTambahUbahActionPerformed

    private void ButtonResetHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonResetHapusActionPerformed
        if (state.equals("edit")) {
            hapusData();
            loadTable();
        }

        resetForm();
    }//GEN-LAST:event_ButtonResetHapusActionPerformed

    private void TextCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextCariActionPerformed
        cari();
    }//GEN-LAST:event_TextCariActionPerformed

    private void NamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NamaActionPerformed

    private void NIKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NIKActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NIKActionPerformed

    private void AgamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AgamaActionPerformed

    private void PendidikanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PendidikanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PendidikanActionPerformed

    private void PekerjaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PekerjaanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PekerjaanActionPerformed

    private void KawinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KawinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KawinActionPerformed

    private void TempatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TempatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TempatActionPerformed

    private void lakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lakiActionPerformed
        perempuan.setSelected(false);
    }//GEN-LAST:event_lakiActionPerformed

    private void perempuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_perempuanActionPerformed
        laki.setSelected(false);
    }//GEN-LAST:event_perempuanActionPerformed

    private void JabatanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JabatanItemStateChanged

    }//GEN-LAST:event_JabatanItemStateChanged

    private void JabatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JabatanActionPerformed
        comboJabatanIndex = Jabatan.getSelectedIndex();
        if (comboJabatanIndex >= 0) {
            selectedComboJabatanIndex = comboJabatanID.get(comboJabatanIndex);
        }
    }//GEN-LAST:event_JabatanActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Karyawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Karyawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Karyawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Karyawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Karyawan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Agama;
    private javax.swing.JButton ButtonRefresh;
    private javax.swing.JButton ButtonResetHapus;
    private javax.swing.JButton ButtonTambahUbah;
    private javax.swing.JComboBox<String> Jabatan;
    private javax.swing.JTextField Kawin;
    private javax.swing.JLabel LabelCari;
    private javax.swing.JLabel LabelCari1;
    private javax.swing.JLabel LabelCari10;
    private javax.swing.JLabel LabelCari2;
    private javax.swing.JLabel LabelCari3;
    private javax.swing.JLabel LabelCari4;
    private javax.swing.JLabel LabelCari5;
    private javax.swing.JLabel LabelCari6;
    private javax.swing.JLabel LabelCari7;
    private javax.swing.JLabel LabelCari8;
    private javax.swing.JLabel LabelCari9;
    private javax.swing.JTextField NIK;
    private javax.swing.JTextField Nama;
    private javax.swing.JTextField Pekerjaan;
    private javax.swing.JTextField Pendidikan;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JTable TablePegawai;
    private com.toedter.calendar.JDateChooser TanggalLahir;
    private javax.swing.JTextField Tempat;
    private javax.swing.JTextField TextCari;
    private javax.swing.JRadioButton laki;
    private javax.swing.JRadioButton perempuan;
    // End of variables declaration//GEN-END:variables
}
