IF OBJECT_ID('dbo.Landmark', 'U') IS NOT NULL 
  DROP TABLE dbo.Landmark;

CREATE TABLE Landmark(
ID INT CHECK(ID>=0) PRIMARY KEY,
TITLE VARCHAR(30) NOT NULL,
LATITUDE FLOAT NOT NULL,
LONGITUDE FLOAT NOT NULL,
DESCRIPT VARCHAR(500) NOT NULL
);
