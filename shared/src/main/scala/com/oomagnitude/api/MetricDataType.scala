package com.oomagnitude.api

import upickle.key

sealed trait MetricDataType
@key("number") 
case object Number extends MetricDataType
@key("mutualInformation") 
case object MutualInfoType extends MetricDataType