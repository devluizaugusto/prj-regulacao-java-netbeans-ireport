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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 * Tela de cadastro de Usuários
 *
 * @author Luiz Augusto de Andrade Silva
 */
public class TelaUsuario extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    private static TelaUsuario telaUsuario;

    public static TelaUsuario getInstancia() {
        if (telaUsuario == null) {
            telaUsuario = new TelaUsuario();
        }
        return telaUsuario;
    }

    public TelaUsuario() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void listarUsuario() {
        DefaultTableModel modelo = (DefaultTableModel) tblUsuario.getModel();
        modelo.setNumRows(0);
        tblUsuario.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblUsuario.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblUsuario.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblUsuario.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblUsuario.getColumnModel().getColumn(4).setPreferredWidth(30);

        String sql = "select * from usuario";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5)
                });
            }
            btAdicionarUsu.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisarUsuario() {
       String sql = "select Id, Nome, Login, Senha, Perfil from usuario where Nome like ?;";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarUsu.getText() + "%");
            rs = pst.executeQuery();
            
            tblUsuario.setModel(DbUtils.resultSetToTableModel(rs));
            
            btAdicionarUsu.setEnabled(false);
           
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void setarCamposUsuario() {
        int setarCampos = tblUsuario.getSelectedRow();
        txtIdUsu.setText(tblUsuario.getModel().getValueAt(setarCampos, 0).toString());
        txtNomeUsu.setText(tblUsuario.getModel().getValueAt(setarCampos, 1).toString());
        txtLoginUsu.setText(tblUsuario.getModel().getValueAt(setarCampos, 2).toString());
        txtSenhaUsu.setText(tblUsuario.getModel().getValueAt(setarCampos, 3).toString());
        cbPerfilUsu.setSelectedItem(tblUsuario.getModel().getValueAt(setarCampos, 4).toString());
    }

    private void adicionarUsuario() {
        String sql = "INSERT INTO usuario(Nome, Login, Senha, Perfil) VALUES(?, ?, ?, ?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNomeUsu.getText());
            pst.setString(2, txtLoginUsu.getText());
            String senha = new String(txtSenhaUsu.getPassword());
            pst.setString(3, senha);
            pst.setString(4, cbPerfilUsu.getSelectedItem().toString());

            if ((txtNomeUsu.getText().isEmpty()) || (txtLoginUsu.getText().isEmpty()) || (txtSenhaUsu.getPassword() == null) || cbPerfilUsu.getSelectedItem().equals("")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");
                    limparCampos();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Usuário não cadastrado!");
        }
    }

    private void atualizarUsuario() {
        String sql = "UPDATE usuario SET Nome = ?, Login = ?, Senha = ?, Perfil = ? WHERE Id = ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtNomeUsu.getText());
            pst.setString(2, txtLoginUsu.getText());
            String senha = new String(txtSenhaUsu.getPassword());
            pst.setString(3, senha);
            pst.setString(4, cbPerfilUsu.getSelectedItem().toString());
            pst.setString(5, txtIdUsu.getText());

            if ((txtIdUsu.getText().isEmpty()) || (txtNomeUsu.getText().isEmpty()) || (txtLoginUsu.getText().isEmpty()) || (txtSenhaUsu.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int atualizado = pst.executeUpdate();
                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário atualizado com sucesso!");
                    limparCampos();
                    btAdicionarUsu.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void deletarUsuario() {
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja remover esse usuário", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM usuario WHERE Id = ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdUsu.getText());

                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário removido com sucesso!");
                    limparCampos();
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um usuário da tabela para remover!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void limparCampos() {
        txtIdUsu.setText(null);
        txtNomeUsu.setText(null);
        txtLoginUsu.setText(null);
        txtSenhaUsu.setText(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtIdUsu = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNomeUsu = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtLoginUsu = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cbPerfilUsu = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        btAdicionarUsu = new javax.swing.JButton();
        btPesquisarUsu = new javax.swing.JButton();
        btAlterarUsu = new javax.swing.JButton();
        btDeletarUsu = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuario = new javax.swing.JTable();
        txtPesquisarUsu = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSenhaUsu = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Usuários");
        setPreferredSize(new java.awt.Dimension(640, 480));

        jLabel1.setText("*Id");

        txtIdUsu.setEditable(false);

        jLabel2.setText("*Nome");

        jLabel3.setText("*Login");

        jLabel4.setText("*Senha");

        cbPerfilUsu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Administrador", "Usuario" }));

        jLabel5.setText("*Perfil");

        btAdicionarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Create.png"))); // NOI18N
        btAdicionarUsu.setToolTipText("Adicionar");
        btAdicionarUsu.setPreferredSize(new java.awt.Dimension(80, 80));
        btAdicionarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAdicionarUsuActionPerformed(evt);
            }
        });

        btPesquisarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Read.png"))); // NOI18N
        btPesquisarUsu.setToolTipText("Pesquisar");
        btPesquisarUsu.setMinimumSize(new java.awt.Dimension(33, 9));
        btPesquisarUsu.setPreferredSize(new java.awt.Dimension(80, 80));
        btPesquisarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPesquisarUsuActionPerformed(evt);
            }
        });

        btAlterarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Update.png"))); // NOI18N
        btAlterarUsu.setToolTipText("Atualizar");
        btAlterarUsu.setPreferredSize(new java.awt.Dimension(80, 80));
        btAlterarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAlterarUsuActionPerformed(evt);
            }
        });

        btDeletarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Delete.png"))); // NOI18N
        btDeletarUsu.setToolTipText("Deletar");
        btDeletarUsu.setPreferredSize(new java.awt.Dimension(80, 80));
        btDeletarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeletarUsuActionPerformed(evt);
            }
        });

        tblUsuario = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Nome", "Login", "Senha", "Perfil"
            }
        ));
        tblUsuario.getTableHeader().setReorderingAllowed(false);
        tblUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuario);

        txtPesquisarUsu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarUsuKeyReleased(evt);
            }
        });

        jLabel6.setText("*Campos Obrigatórios");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/regulacao/icones/Search.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 723, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btAdicionarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(70, 70, 70)
                                    .addComponent(btPesquisarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(63, 63, 63)
                                    .addComponent(btAlterarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(63, 63, 63)
                                    .addComponent(btDeletarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(txtNomeUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(txtSenhaUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(74, 74, 74)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel5)
                                        .addComponent(txtLoginUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbPerfilUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(33, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtPesquisarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(57, 57, 57))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btAdicionarUsu, btAlterarUsu, btDeletarUsu, btPesquisarUsu});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtPesquisarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNomeUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLoginUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbPerfilUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSenhaUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btPesquisarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btAlterarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btDeletarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btAdicionarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)))))
                .addGap(41, 41, 41))
        );

        setBounds(0, 0, 795, 541);
    }// </editor-fold>//GEN-END:initComponents

    private void btAdicionarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAdicionarUsuActionPerformed
        adicionarUsuario();
    }//GEN-LAST:event_btAdicionarUsuActionPerformed

    private void btPesquisarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPesquisarUsuActionPerformed
        listarUsuario();
    }//GEN-LAST:event_btPesquisarUsuActionPerformed

    private void btAlterarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAlterarUsuActionPerformed
        atualizarUsuario();
    }//GEN-LAST:event_btAlterarUsuActionPerformed

    private void btDeletarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDeletarUsuActionPerformed
        deletarUsuario();
    }//GEN-LAST:event_btDeletarUsuActionPerformed

    private void tblUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuarioMouseClicked
        setarCamposUsuario();
    }//GEN-LAST:event_tblUsuarioMouseClicked

    private void txtPesquisarUsuKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarUsuKeyReleased
        pesquisarUsuario();
    }//GEN-LAST:event_txtPesquisarUsuKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdicionarUsu;
    private javax.swing.JButton btAlterarUsu;
    private javax.swing.JButton btDeletarUsu;
    private javax.swing.JButton btPesquisarUsu;
    private javax.swing.JComboBox cbPerfilUsu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUsuario;
    private javax.swing.JTextField txtIdUsu;
    private javax.swing.JTextField txtLoginUsu;
    private javax.swing.JTextField txtNomeUsu;
    private javax.swing.JTextField txtPesquisarUsu;
    private javax.swing.JPasswordField txtSenhaUsu;
    // End of variables declaration//GEN-END:variables
}
