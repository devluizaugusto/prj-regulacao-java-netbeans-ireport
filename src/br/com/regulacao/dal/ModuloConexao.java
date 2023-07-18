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
package br.com.regulacao.dal;

import java.sql.*;

/**
 * Conexão com o banco de dados
 * @author Luiz Augusto de Andrade Silva
 */
public class ModuloConexao {

    /**
     * Metodo responsavél pela conexão com o banco de dados
     *
     * @return conexao
     */
    public static Connection conector() {
        Connection conexao = null;

        //A linha abaixo chama o Driver.
        String driver = "com.mysql.cj.jdbc.Driver";

        //Armazenando atualizações referente ao banco de dados.
        String url = "jdbc:mysql://localhost:3306/dbregulacao?characterEncoding=utf-8";
        String user = "dba";
        String password = "Regulacao123456";

        //Estabelecendo conexao com o banco de dados.
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } 
        catch (Exception e) {
            //System.out.println(e);
            return null;
        }
    }
}
