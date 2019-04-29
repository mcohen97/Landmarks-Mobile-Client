﻿using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using System.Collections.Generic;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class ToursRepositoryTest
    {
        private SqlServerLandmarksRepository landmarks;
        private SqlServerToursRepository tours;
        private TestDatabaseManager testData;
        private ISqlContext context;

        [TestInitialize]
        public void SetUp() {
            testData = new TestDatabaseManager();
            testData.SetUpDatabase();
            testData.LoadTestData();
            context = new SqlServerConnectionManager(testData.ConnectionString);
            landmarks = new SqlServerLandmarksRepository(context, testData.ImagesPath, testData.AudiosPath);
            tours = new SqlServerToursRepository(context,landmarks);
        }

        [TestMethod]
        public void ShouldReturnTourGivenExistingId() {
            Tour retrieved = tours.GetById(1);
            Assert.AreEqual(1, retrieved.Id);
        }

        [TestMethod]
        [ExpectedException(typeof(TourNotFoundException))]
        public void ShouldThrowExceptionIfTourIsUnexistent() {
            Tour retrieved = tours.GetById(101);
        }

        [TestMethod]
        public void ShouldReturnTourWhoseStopsAreInRange() {
            double lat = -34.923844;
            double lng = -56.170590;
            double distance = 3;

            ICollection<Tour> retrieved = tours.GetToursWithinKmRange(lat, lng, distance);
            Assert.AreEqual(1, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnToursWhoseStopsAreInRange()
        {
            double lat = -34.923844;
            double lng = -56.170590;
            double distance = 20;

            ICollection<Tour> retrieved = tours.GetToursWithinKmRange(lat, lng, distance);
            Assert.AreEqual(2, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnNoToursIfNoneIsWithinRange() {
            double lat = -34.923844;
            double lng = -56.170590;
            double distance = 1;

            ICollection<Tour> retrieved = tours.GetToursWithinKmRange(lat, lng, distance);
            Assert.AreEqual(0, retrieved.Count);
        }
    }
}
