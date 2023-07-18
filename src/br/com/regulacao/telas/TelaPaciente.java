/*
 * The MIT License
 *
 * Copyright 2023 Desenvolvido por Luiz Augusto de Andrade Silva.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.regulacao.telas;

import java.sql.*;
import br.com.regulacao.dal.ModuloConexao;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Tela de cadastro de Paciente
 *
 * @author Luiz Augusto
 */
public class TelaPaciente extends javax.swing.JInternalFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private static TelaPaciente telaPaciente;
    
    public static TelaPaciente getInstancia() {
        if (telaPaciente == null) {
            telaPaciente = new TelaPaciente();
        }
        return telaPaciente;
    }
    
    public TelaPaciente() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    private void adicionarPaciente() {
        String sql = "insert into paciente(Nome, CPF, Data_Nascimento, Descricao) values(?, ?, ?, ?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNomePac.getText());
            pst.setString(2, ftCpfPac.getText());
            pst.setString(3, ftDataNascPac.getText());
            pst.setString(4, taDescricaoPac.getText());
            
            if (txtNomePac.getText().isEmpty() || (ftCpfPac.getText() == null || ftCpfPac.getText().isEmpty()) || (ftDataNascPac.getText() == null || ftDataNascPac.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Paciente cadastrado com sucesso!");
                    limparCampos();
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Paciente não cadastrado! Dados do paciente repetido com de outro paciente ja cadastrado!!");
        }
    }
    
    private void listarPacientes() {
        DefaultTableModel modelo = (DefaultTableModel) tblPaciente.getModel();
        modelo.setNumRows(0);
        
        tblPaciente.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblPaciente.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblPaciente.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblPaciente.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblPaciente.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblPaciente.getColumnModel().getColumn(5).setPreferredWidth(50);
        
        String sql = "select Id, Nome, CPF, Data_Nascimento as Data_de_Nascimento, Descricao, date_format(Data_Registro, '%d/%m/%Y') as Data_do_Registro from paciente;";
        
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6)
                });
            }
            btnAdicionarPac.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void pesquisarPaciente() {
        String sql = "select Id, Nome, CPF, Data_Nascimento as Data_de_Nascimento, Descricao, date_format(Data_Registro, '%d/%m/%Y') as Data_do_Registro from paciente where Nome like ?;";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarPac.getText() + "%");
            rs = pst.executeQuery();
            
            tblPaciente.setModel(DbUtils.resultSetToTableModel(rs));
            
            btnAdicionarPac.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void setarCamposPaciente() {
        int setarCampos = tblPaciente.getSelectedRow();
        txtIdPac.setText(tblPaciente.getModel().getValueAt(setarCampos, 0).toString());
        txtNomePac.setText(tblPaciente.getModel().getValueAt(setarCampos, 1).toString());
        ftCpfPac.setText(tblPaciente.getModel().getValueAt(setarCampos, 2).toString());
        ftDataNascPac.setText(tblPaciente.getModel().getValueAt(setarCampos, 3).toString());
        taDescricaoPac.setText(tblPaciente.getModel().getValueAt(setarCampos, 4).toString());
        txtDataRegistro.setText(tblPaciente.getModel().getValueAt(setarCampos, 5).toString());   
    }
    
    private void atualizarPaciente() {
        String sql = "update paciente set Nome = ?, CPF = ?, Data_Nascimento = ?, Descricao = ? where Id = ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNomePac.getText());
            pst.setString(2, ftCpfPac.getText());
            pst.setString(3, ftDataNascPac.getText());
            pst.setString(4, taDescricaoPac.getText());
            pst.setString(5, txtIdPac.getText());
            
            if (txtNomePac.getText().isEmpty() || (ftCpfPac.getText() == null || ftCpfPac.getText().isEmpty()) || (ftDataNascPac.getText() == null || ftDataNascPac.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int atualizado = pst.executeUpdate();
                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(null, "Paciente atualizado com sucesso!");
                    limparCampos();                    
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void deletarPaciente() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover esse paciente?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from paciente where Id = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdPac.getText());
                
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Paciente removido com sucesso!");
                    limparCampos();
                } else {
                    JOptionPane.showMessageDialog(null, "Não pode remover um paciente não cadastrado!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void gerarRelatorio() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a emissão desse relatório?", "Atenção", JOptionPane.YES_NO_OPTION);
        
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                HashMap filtro = new HashMap();
                filtro.put("Id", Integer.parseInt(txtIdPac.getText()));
                
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/relatorioIndividualPaciente.jasper"), filtro, conexao);
                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Faça uma busca do Paciente para gerar o relatório!");
            }            
        }
    }
    
    private void limparCampos() {
        txtPesquisarPac.setText(null);
        txtIdPac.setText(null);
        txtNomePac.setText(null);
        ftCpfPac.setText(null);
        ftDataNascPac.setText(null);
        taDescricaoPac.setText(null);
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblPaciente = new javax.swing.JTable();
        txtPesquisarPac = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNomePac = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ftCpfPac = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        ftDataNascPac = new javax.swing.JFormattedTextField();
        btnAdicionarPac = new javax.swing.JButton();
        btnAlterarPac = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnImprimirPac = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        taDescricaoPac = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDataRegistro = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnPesquisarPac = new javax.swing.JButton();
        txtIdPac = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Pacientes");
        setPreferredSize(new java.awt.Dimension(640, 481));

        tblPaciente = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblPaciente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Nome", "CPF", "Data de Nascimento", "Descrição", "Data do Registro"
            }
        ));
        tblPaciente.getTableHeader().setReorderingAllowed(false);
        tblPaciente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPacienteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPaciente);

        txtPesquisarPac.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarPacKeyReleased(evt);
            }
        });

        jLabel1.setText("*Campos Obrigatórios");

        jLabel2.setText("*Nome");

        jLabel3.setText("*CPF");

        try {
            ftCpfPac.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel4.setText("*Data de Nascimento");

        try {
            ftDataNascPac.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        btnAdicionarPac.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Create.png"))); // NOI18N
        btnAdicionarPac.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAdicionarPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarPacActionPerformed(evt);
            }
        });

        btnAlterarPac.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Update.png"))); // NOI18N
        btnAlterarPac.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlterarPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarPacActionPerformed(evt);
            }
        });

        jLabel5.setText("Id");

        btnImprimirPac.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Print.png"))); // NOI18N
        btnImprimirPac.setPreferredSize(new java.awt.Dimension(80, 80));
        btnImprimirPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirPacActionPerformed(evt);
            }
        });

        taDescricaoPac.setColumns(20);
        taDescricaoPac.setRows(5);
        jScrollPane2.setViewportView(taDescricaoPac);

        jLabel6.setText("Descrição");

        jLabel7.setText("Data do Registro");

        txtDataRegistro.setEditable(false);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Search.png"))); // NOI18N

        btnPesquisarPac.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Read.png"))); // NOI18N
        btnPesquisarPac.setToolTipText("Pesquisar");
        btnPesquisarPac.setMinimumSize(new java.awt.Dimension(33, 9));
        btnPesquisarPac.setPreferredSize(new java.awt.Dimension(80, 80));
        btnPesquisarPac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarPacActionPerformed(evt);
            }
        });

        txtIdPac.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(txtPesquisarPac, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(94, 94, 94))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftCpfPac, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdPac, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtNomePac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(ftDataNascPac, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addComponent(jLabel7)
                            .addComponent(txtDataRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(101, 101, 101))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAdicionarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(btnPesquisarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(btnAlterarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(btnImprimirPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(txtPesquisarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(45, 45, 45)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ftCpfPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNomePac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addGap(26, 26, 26))
                            .addComponent(ftDataNascPac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDataRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAlterarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdicionarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnImprimirPac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        setBounds(0, 0, 795, 540);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarPacKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarPacKeyReleased
        pesquisarPaciente();
    }//GEN-LAST:event_txtPesquisarPacKeyReleased

    private void tblPacienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPacienteMouseClicked
        setarCamposPaciente();
    }//GEN-LAST:event_tblPacienteMouseClicked

    private void btnAdicionarPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarPacActionPerformed
        adicionarPaciente();
    }//GEN-LAST:event_btnAdicionarPacActionPerformed

    private void btnAlterarPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarPacActionPerformed
        atualizarPaciente();
    }//GEN-LAST:event_btnAlterarPacActionPerformed

    private void btnImprimirPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirPacActionPerformed
        gerarRelatorio();
    }//GEN-LAST:event_btnImprimirPacActionPerformed

    private void btnPesquisarPacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarPacActionPerformed
        listarPacientes();
    }//GEN-LAST:event_btnPesquisarPacActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarPac;
    private javax.swing.JButton btnAlterarPac;
    private javax.swing.JButton btnImprimirPac;
    private javax.swing.JButton btnPesquisarPac;
    private javax.swing.JFormattedTextField ftCpfPac;
    private javax.swing.JFormattedTextField ftDataNascPac;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea taDescricaoPac;
    private javax.swing.JTable tblPaciente;
    private javax.swing.JTextField txtDataRegistro;
    private javax.swing.JTextField txtIdPac;
    private javax.swing.JTextField txtNomePac;
    private javax.swing.JTextField txtPesquisarPac;
    // End of variables declaration//GEN-END:variables
}
