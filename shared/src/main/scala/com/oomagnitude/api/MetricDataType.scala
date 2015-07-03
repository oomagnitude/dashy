package com.oomagnitude.api

import upickle.key

sealed trait MetricDataType
@key("number") 
case object Number extends MetricDataType
@key("custom")
case object CustomType extends MetricDataType