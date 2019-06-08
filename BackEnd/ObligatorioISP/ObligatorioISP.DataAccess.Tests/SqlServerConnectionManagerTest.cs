﻿using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class SqlServerConnectionManagerTest
    {
        private SqlServerConnectionManager connection;
        [TestInitialize]
        public void SetUp() {
          string unexistentServer = "Server=DESKTOP-JH1M2MF\\SQLSERVER_R14;Initial Catalog=unexistentDB;Trusted_Connection=True;Integrated Security=True;";
          connection = new SqlServerConnectionManager(unexistentServer);
        }

        [TestMethod]
        [ExpectedException(typeof(DataInaccessibleException))]
        public void ShouldThrowExceptionIfCantReadData() {
            string query = "query";
            connection.ExcecuteRead(query);
        }

        [TestMethod]
        [ExpectedException(typeof(DataInaccessibleException))]
        public void ShouldThrowExceptionIfCantExcecuteCommmand() {
            string command = "command";
            connection.ExcecuteCommand(command);
        }
    }
}
