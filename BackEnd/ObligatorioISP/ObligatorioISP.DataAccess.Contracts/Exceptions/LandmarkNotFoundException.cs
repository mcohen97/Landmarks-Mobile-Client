﻿using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts.Exceptions
{
    public class LandmarkNotFoundException : Exception
    {
        public LandmarkNotFoundException(string message) : base(message)
        {
        }
    }
}
