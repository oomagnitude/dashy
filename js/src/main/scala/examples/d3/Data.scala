package examples.d3

import upickle.Js.Value
import upickle.{Js, default => upickle}
import viz.layout.Tree

object Data {
  object TreeNode {
    implicit val reader = new upickle.Reader[TreeNode] {
      override def read0: PartialFunction[Value, TreeNode] = {
        case Js.Obj(("name", Js.Str(name)), ("children", children: Js.Arr)) =>
          TreeNode(name, children.value.map(read0), None)
        case Js.Obj(("name", Js.Str(name)), ("size", Js.Num(size))) =>
          TreeNode(name, Seq.empty, Some(size.toInt))
      }
    }
  }
  case class TreeNode(name: String, override val children: Seq[TreeNode], size: Option[Int]) extends Tree {
    override val id: String = name
  }

  val treeJson =
    """
      |{
      | "name": "flare",
      | "children": [
      |  {
      |   "name": "analytics",
      |   "children": [
      |    {
      |     "name": "cluster",
      |     "children": [
      |      {"name": "AgglomerativeCluster", "size": 3938},
      |      {"name": "CommunityStructure", "size": 3812},
      |      {"name": "HierarchicalCluster", "size": 6714},
      |      {"name": "MergeEdge", "size": 743}
      |     ]
      |    },
      |    {
      |     "name": "graph",
      |     "children": [
      |      {"name": "BetweennessCentrality", "size": 3534},
      |      {"name": "LinkDistance", "size": 5731},
      |      {"name": "MaxFlowMinCut", "size": 7840},
      |      {"name": "ShortestPaths", "size": 5914},
      |      {"name": "SpanningTree", "size": 3416}
      |     ]
      |    },
      |    {
      |     "name": "optimization",
      |     "children": [
      |      {"name": "AspectRatioBanker", "size": 7074}
      |     ]
      |    }
      |   ]
      |  },
      |  {
      |   "name": "animate",
      |   "children": [
      |    {"name": "Easing", "size": 17010},
      |    {"name": "FunctionSequence", "size": 5842},
      |    {
      |     "name": "interpolate",
      |     "children": [
      |      {"name": "ArrayInterpolator", "size": 1983},
      |      {"name": "ColorInterpolator", "size": 2047},
      |      {"name": "DateInterpolator", "size": 1375},
      |      {"name": "Interpolator", "size": 8746},
      |      {"name": "MatrixInterpolator", "size": 2202},
      |      {"name": "NumberInterpolator", "size": 1382},
      |      {"name": "ObjectInterpolator", "size": 1629},
      |      {"name": "PointInterpolator", "size": 1675},
      |      {"name": "RectangleInterpolator", "size": 2042}
      |     ]
      |    },
      |    {"name": "ISchedulable", "size": 1041},
      |    {"name": "Parallel", "size": 5176},
      |    {"name": "Pause", "size": 449},
      |    {"name": "Scheduler", "size": 5593},
      |    {"name": "Sequence", "size": 5534},
      |    {"name": "Transition", "size": 9201},
      |    {"name": "Transitioner", "size": 19975},
      |    {"name": "TransitionEvent", "size": 1116},
      |    {"name": "Tween", "size": 6006}
      |   ]
      |  },
      |  {
      |   "name": "data",
      |   "children": [
      |    {
      |     "name": "converters",
      |     "children": [
      |      {"name": "Converters", "size": 721},
      |      {"name": "DelimitedTextConverter", "size": 4294},
      |      {"name": "GraphMLConverter", "size": 9800},
      |      {"name": "IDataConverter", "size": 1314},
      |      {"name": "JSONConverter", "size": 2220}
      |     ]
      |    },
      |    {"name": "DataField", "size": 1759},
      |    {"name": "DataSchema", "size": 2165},
      |    {"name": "DataSet", "size": 586},
      |    {"name": "DataSource", "size": 3331},
      |    {"name": "DataTable", "size": 772},
      |    {"name": "DataUtil", "size": 3322}
      |   ]
      |  },
      |  {
      |   "name": "display",
      |   "children": [
      |    {"name": "DirtySprite", "size": 8833},
      |    {"name": "LineSprite", "size": 1732},
      |    {"name": "RectSprite", "size": 3623},
      |    {"name": "TextSprite", "size": 10066}
      |   ]
      |  },
      |  {
      |   "name": "flex",
      |   "children": [
      |    {"name": "FlareVis", "size": 4116}
      |   ]
      |  },
      |  {
      |   "name": "physics",
      |   "children": [
      |    {"name": "DragForce", "size": 1082},
      |    {"name": "GravityForce", "size": 1336},
      |    {"name": "IForce", "size": 319},
      |    {"name": "NBodyForce", "size": 10498},
      |    {"name": "Particle", "size": 2822},
      |    {"name": "Simulation", "size": 9983},
      |    {"name": "Spring", "size": 2213},
      |    {"name": "SpringForce", "size": 1681}
      |   ]
      |  },
      |  {
      |   "name": "query",
      |   "children": [
      |    {"name": "AggregateExpression", "size": 1616},
      |    {"name": "And", "size": 1027},
      |    {"name": "Arithmetic", "size": 3891},
      |    {"name": "Average", "size": 891},
      |    {"name": "BinaryExpression", "size": 2893},
      |    {"name": "Comparison", "size": 5103},
      |    {"name": "CompositeExpression", "size": 3677},
      |    {"name": "Count", "size": 781},
      |    {"name": "DateUtil", "size": 4141},
      |    {"name": "Distinct", "size": 933},
      |    {"name": "Expression", "size": 5130},
      |    {"name": "ExpressionIterator", "size": 3617},
      |    {"name": "Fn", "size": 3240},
      |    {"name": "If", "size": 2732},
      |    {"name": "IsA", "size": 2039},
      |    {"name": "Literal", "size": 1214},
      |    {"name": "Match", "size": 3748},
      |    {"name": "Maximum", "size": 843},
      |    {
      |     "name": "methods",
      |     "children": [
      |      {"name": "add", "size": 593},
      |      {"name": "and", "size": 330},
      |      {"name": "average", "size": 287},
      |      {"name": "count", "size": 277},
      |      {"name": "distinct", "size": 292},
      |      {"name": "div", "size": 595},
      |      {"name": "eq", "size": 594},
      |      {"name": "fn", "size": 460},
      |      {"name": "gt", "size": 603},
      |      {"name": "gte", "size": 625},
      |      {"name": "iff", "size": 748},
      |      {"name": "isa", "size": 461},
      |      {"name": "lt", "size": 597},
      |      {"name": "lte", "size": 619},
      |      {"name": "max", "size": 283},
      |      {"name": "min", "size": 283},
      |      {"name": "mod", "size": 591},
      |      {"name": "mul", "size": 603},
      |      {"name": "neq", "size": 599},
      |      {"name": "not", "size": 386},
      |      {"name": "or", "size": 323},
      |      {"name": "orderby", "size": 307},
      |      {"name": "range", "size": 772},
      |      {"name": "select", "size": 296},
      |      {"name": "stddev", "size": 363},
      |      {"name": "sub", "size": 600},
      |      {"name": "sum", "size": 280},
      |      {"name": "update", "size": 307},
      |      {"name": "variance", "size": 335},
      |      {"name": "where", "size": 299},
      |      {"name": "xor", "size": 354},
      |      {"name": "_", "size": 264}
      |     ]
      |    },
      |    {"name": "Minimum", "size": 843},
      |    {"name": "Not", "size": 1554},
      |    {"name": "Or", "size": 970},
      |    {"name": "Query", "size": 13896},
      |    {"name": "Range", "size": 1594},
      |    {"name": "StringUtil", "size": 4130},
      |    {"name": "Sum", "size": 791},
      |    {"name": "Variable", "size": 1124},
      |    {"name": "Variance", "size": 1876},
      |    {"name": "Xor", "size": 1101}
      |   ]
      |  },
      |  {
      |   "name": "scale",
      |   "children": [
      |    {"name": "IScaleMap", "size": 2105},
      |    {"name": "LinearScale", "size": 1316},
      |    {"name": "LogScale", "size": 3151},
      |    {"name": "OrdinalScale", "size": 3770},
      |    {"name": "QuantileScale", "size": 2435},
      |    {"name": "QuantitativeScale", "size": 4839},
      |    {"name": "RootScale", "size": 1756},
      |    {"name": "Scale", "size": 4268},
      |    {"name": "ScaleType", "size": 1821},
      |    {"name": "TimeScale", "size": 5833}
      |   ]
      |  },
      |  {
      |   "name": "util",
      |   "children": [
      |    {"name": "Arrays", "size": 8258},
      |    {"name": "Colors", "size": 10001},
      |    {"name": "Dates", "size": 8217},
      |    {"name": "Displays", "size": 12555},
      |    {"name": "Filter", "size": 2324},
      |    {"name": "Geometry", "size": 10993},
      |    {
      |     "name": "heap",
      |     "children": [
      |      {"name": "FibonacciHeap", "size": 9354},
      |      {"name": "HeapNode", "size": 1233}
      |     ]
      |    },
      |    {"name": "IEvaluable", "size": 335},
      |    {"name": "IPredicate", "size": 383},
      |    {"name": "IValueProxy", "size": 874},
      |    {
      |     "name": "math",
      |     "children": [
      |      {"name": "DenseMatrix", "size": 3165},
      |      {"name": "IMatrix", "size": 2815},
      |      {"name": "SparseMatrix", "size": 3366}
      |     ]
      |    },
      |    {"name": "Maths", "size": 17705},
      |    {"name": "Orientation", "size": 1486},
      |    {
      |     "name": "palette",
      |     "children": [
      |      {"name": "ColorPalette", "size": 6367},
      |      {"name": "Palette", "size": 1229},
      |      {"name": "ShapePalette", "size": 2059},
      |      {"name": "SizePalette", "size": 2291}
      |     ]
      |    },
      |    {"name": "Property", "size": 5559},
      |    {"name": "Shapes", "size": 19118},
      |    {"name": "Sort", "size": 6887},
      |    {"name": "Stats", "size": 6557},
      |    {"name": "Strings", "size": 22026}
      |   ]
      |  },
      |  {
      |   "name": "vis",
      |   "children": [
      |    {
      |     "name": "axis",
      |     "children": [
      |      {"name": "Axes", "size": 1302},
      |      {"name": "Axis", "size": 24593},
      |      {"name": "AxisGridLine", "size": 652},
      |      {"name": "AxisLabel", "size": 636},
      |      {"name": "CartesianAxes", "size": 6703}
      |     ]
      |    },
      |    {
      |     "name": "controls",
      |     "children": [
      |      {"name": "AnchorControl", "size": 2138},
      |      {"name": "ClickControl", "size": 3824},
      |      {"name": "Control", "size": 1353},
      |      {"name": "ControlList", "size": 4665},
      |      {"name": "DragControl", "size": 2649},
      |      {"name": "ExpandControl", "size": 2832},
      |      {"name": "HoverControl", "size": 4896},
      |      {"name": "IControl", "size": 763},
      |      {"name": "PanZoomControl", "size": 5222},
      |      {"name": "SelectionControl", "size": 7862},
      |      {"name": "TooltipControl", "size": 8435}
      |     ]
      |    },
      |    {
      |     "name": "data",
      |     "children": [
      |      {"name": "Data", "size": 20544},
      |      {"name": "DataList", "size": 19788},
      |      {"name": "DataSprite", "size": 10349},
      |      {"name": "EdgeSprite", "size": 3301},
      |      {"name": "NodeSprite", "size": 19382},
      |      {
      |       "name": "render",
      |       "children": [
      |        {"name": "ArrowType", "size": 698},
      |        {"name": "EdgeRenderer", "size": 5569},
      |        {"name": "IRenderer", "size": 353},
      |        {"name": "ShapeRenderer", "size": 2247}
      |       ]
      |      },
      |      {"name": "ScaleBinding", "size": 11275},
      |      {"name": "Tree", "size": 7147},
      |      {"name": "TreeBuilder", "size": 9930}
      |     ]
      |    },
      |    {
      |     "name": "events",
      |     "children": [
      |      {"name": "DataEvent", "size": 2313},
      |      {"name": "SelectionEvent", "size": 1880},
      |      {"name": "TooltipEvent", "size": 1701},
      |      {"name": "VisualizationEvent", "size": 1117}
      |     ]
      |    },
      |    {
      |     "name": "legend",
      |     "children": [
      |      {"name": "Legend", "size": 20859},
      |      {"name": "LegendItem", "size": 4614},
      |      {"name": "LegendRange", "size": 10530}
      |     ]
      |    },
      |    {
      |     "name": "operator",
      |     "children": [
      |      {
      |       "name": "distortion",
      |       "children": [
      |        {"name": "BifocalDistortion", "size": 4461},
      |        {"name": "Distortion", "size": 6314},
      |        {"name": "FisheyeDistortion", "size": 3444}
      |       ]
      |      },
      |      {
      |       "name": "encoder",
      |       "children": [
      |        {"name": "ColorEncoder", "size": 3179},
      |        {"name": "Encoder", "size": 4060},
      |        {"name": "PropertyEncoder", "size": 4138},
      |        {"name": "ShapeEncoder", "size": 1690},
      |        {"name": "SizeEncoder", "size": 1830}
      |       ]
      |      },
      |      {
      |       "name": "filter",
      |       "children": [
      |        {"name": "FisheyeTreeFilter", "size": 5219},
      |        {"name": "GraphDistanceFilter", "size": 3165},
      |        {"name": "VisibilityFilter", "size": 3509}
      |       ]
      |      },
      |      {"name": "IOperator", "size": 1286},
      |      {
      |       "name": "label",
      |       "children": [
      |        {"name": "Labeler", "size": 9956},
      |        {"name": "RadialLabeler", "size": 3899},
      |        {"name": "StackedAreaLabeler", "size": 3202}
      |       ]
      |      },
      |      {
      |       "name": "layout",
      |       "children": [
      |        {"name": "AxisLayout", "size": 6725},
      |        {"name": "BundledEdgeRouter", "size": 3727},
      |        {"name": "CircleLayout", "size": 9317},
      |        {"name": "CirclePackingLayout", "size": 12003},
      |        {"name": "DendrogramLayout", "size": 4853},
      |        {"name": "ForceDirectedLayout", "size": 8411},
      |        {"name": "IcicleTreeLayout", "size": 4864},
      |        {"name": "IndentedTreeLayout", "size": 3174},
      |        {"name": "Layout", "size": 7881},
      |        {"name": "NodeLinkTreeLayout", "size": 12870},
      |        {"name": "PieLayout", "size": 2728},
      |        {"name": "RadialTreeLayout", "size": 12348},
      |        {"name": "RandomLayout", "size": 870},
      |        {"name": "StackedAreaLayout", "size": 9121},
      |        {"name": "TreeMapLayout", "size": 9191}
      |       ]
      |      },
      |      {"name": "Operator", "size": 2490},
      |      {"name": "OperatorList", "size": 5248},
      |      {"name": "OperatorSequence", "size": 4190},
      |      {"name": "OperatorSwitch", "size": 2581},
      |      {"name": "SortOperator", "size": 2023}
      |     ]
      |    },
      |    {"name": "Visualization", "size": 16540}
      |   ]
      |  }
      | ]
      |}
      |
    """.stripMargin

  val graphJson =
    """
      |{
      |  "nodes":[
      |    {"name":"Myriel","group":1},
      |    {"name":"Napoleon","group":1},
      |    {"name":"Mlle.Baptistine","group":1},
      |    {"name":"Mme.Magloire","group":1},
      |    {"name":"CountessdeLo","group":1},
      |    {"name":"Geborand","group":1},
      |    {"name":"Champtercier","group":1},
      |    {"name":"Cravatte","group":1},
      |    {"name":"Count","group":1},
      |    {"name":"OldMan","group":1},
      |    {"name":"Labarre","group":2},
      |    {"name":"Valjean","group":2},
      |    {"name":"Marguerite","group":3},
      |    {"name":"Mme.deR","group":2},
      |    {"name":"Isabeau","group":2},
      |    {"name":"Gervais","group":2},
      |    {"name":"Tholomyes","group":3},
      |    {"name":"Listolier","group":3},
      |    {"name":"Fameuil","group":3},
      |    {"name":"Blacheville","group":3},
      |    {"name":"Favourite","group":3},
      |    {"name":"Dahlia","group":3},
      |    {"name":"Zephine","group":3},
      |    {"name":"Fantine","group":3},
      |    {"name":"Mme.Thenardier","group":4},
      |    {"name":"Thenardier","group":4},
      |    {"name":"Cosette","group":5},
      |    {"name":"Javert","group":4},
      |    {"name":"Fauchelevent","group":0},
      |    {"name":"Bamatabois","group":2},
      |    {"name":"Perpetue","group":3},
      |    {"name":"Simplice","group":2},
      |    {"name":"Scaufflaire","group":2},
      |    {"name":"Woman1","group":2},
      |    {"name":"Judge","group":2},
      |    {"name":"Champmathieu","group":2},
      |    {"name":"Brevet","group":2},
      |    {"name":"Chenildieu","group":2},
      |    {"name":"Cochepaille","group":2},
      |    {"name":"Pontmercy","group":4},
      |    {"name":"Boulatruelle","group":6},
      |    {"name":"Eponine","group":4},
      |    {"name":"Anzelma","group":4},
      |    {"name":"Woman2","group":5},
      |    {"name":"MotherInnocent","group":0},
      |    {"name":"Gribier","group":0},
      |    {"name":"Jondrette","group":7},
      |    {"name":"Mme.Burgon","group":7},
      |    {"name":"Gavroche","group":8},
      |    {"name":"Gillenormand","group":5},
      |    {"name":"Magnon","group":5},
      |    {"name":"Mlle.Gillenormand","group":5},
      |    {"name":"Mme.Pontmercy","group":5},
      |    {"name":"Mlle.Vaubois","group":5},
      |    {"name":"Lt.Gillenormand","group":5},
      |    {"name":"Marius","group":8},
      |    {"name":"BaronessT","group":5},
      |    {"name":"Mabeuf","group":8},
      |    {"name":"Enjolras","group":8},
      |    {"name":"Combeferre","group":8},
      |    {"name":"Prouvaire","group":8},
      |    {"name":"Feuilly","group":8},
      |    {"name":"Courfeyrac","group":8},
      |    {"name":"Bahorel","group":8},
      |    {"name":"Bossuet","group":8},
      |    {"name":"Joly","group":8},
      |    {"name":"Grantaire","group":8},
      |    {"name":"MotherPlutarch","group":9},
      |    {"name":"Gueulemer","group":4},
      |    {"name":"Babet","group":4},
      |    {"name":"Claquesous","group":4},
      |    {"name":"Montparnasse","group":4},
      |    {"name":"Toussaint","group":5},
      |    {"name":"Child1","group":10},
      |    {"name":"Child2","group":10},
      |    {"name":"Brujon","group":4},
      |    {"name":"Mme.Hucheloup","group":8}
      |  ],
      |  "links":[
      |    {"source":1,"target":0,"value":1},
      |    {"source":2,"target":0,"value":8},
      |    {"source":3,"target":0,"value":10},
      |    {"source":3,"target":2,"value":6},
      |    {"source":4,"target":0,"value":1},
      |    {"source":5,"target":0,"value":1},
      |    {"source":6,"target":0,"value":1},
      |    {"source":7,"target":0,"value":1},
      |    {"source":8,"target":0,"value":2},
      |    {"source":9,"target":0,"value":1},
      |    {"source":11,"target":10,"value":1},
      |    {"source":11,"target":3,"value":3},
      |    {"source":11,"target":2,"value":3},
      |    {"source":11,"target":0,"value":5},
      |    {"source":12,"target":11,"value":1},
      |    {"source":13,"target":11,"value":1},
      |    {"source":14,"target":11,"value":1},
      |    {"source":15,"target":11,"value":1},
      |    {"source":17,"target":16,"value":4},
      |    {"source":18,"target":16,"value":4},
      |    {"source":18,"target":17,"value":4},
      |    {"source":19,"target":16,"value":4},
      |    {"source":19,"target":17,"value":4},
      |    {"source":19,"target":18,"value":4},
      |    {"source":20,"target":16,"value":3},
      |    {"source":20,"target":17,"value":3},
      |    {"source":20,"target":18,"value":3},
      |    {"source":20,"target":19,"value":4},
      |    {"source":21,"target":16,"value":3},
      |    {"source":21,"target":17,"value":3},
      |    {"source":21,"target":18,"value":3},
      |    {"source":21,"target":19,"value":3},
      |    {"source":21,"target":20,"value":5},
      |    {"source":22,"target":16,"value":3},
      |    {"source":22,"target":17,"value":3},
      |    {"source":22,"target":18,"value":3},
      |    {"source":22,"target":19,"value":3},
      |    {"source":22,"target":20,"value":4},
      |    {"source":22,"target":21,"value":4},
      |    {"source":23,"target":16,"value":3},
      |    {"source":23,"target":17,"value":3},
      |    {"source":23,"target":18,"value":3},
      |    {"source":23,"target":19,"value":3},
      |    {"source":23,"target":20,"value":4},
      |    {"source":23,"target":21,"value":4},
      |    {"source":23,"target":22,"value":4},
      |    {"source":23,"target":12,"value":2},
      |    {"source":23,"target":11,"value":9},
      |    {"source":24,"target":23,"value":2},
      |    {"source":24,"target":11,"value":7},
      |    {"source":25,"target":24,"value":13},
      |    {"source":25,"target":23,"value":1},
      |    {"source":25,"target":11,"value":12},
      |    {"source":26,"target":24,"value":4},
      |    {"source":26,"target":11,"value":31},
      |    {"source":26,"target":16,"value":1},
      |    {"source":26,"target":25,"value":1},
      |    {"source":27,"target":11,"value":17},
      |    {"source":27,"target":23,"value":5},
      |    {"source":27,"target":25,"value":5},
      |    {"source":27,"target":24,"value":1},
      |    {"source":27,"target":26,"value":1},
      |    {"source":28,"target":11,"value":8},
      |    {"source":28,"target":27,"value":1},
      |    {"source":29,"target":23,"value":1},
      |    {"source":29,"target":27,"value":1},
      |    {"source":29,"target":11,"value":2},
      |    {"source":30,"target":23,"value":1},
      |    {"source":31,"target":30,"value":2},
      |    {"source":31,"target":11,"value":3},
      |    {"source":31,"target":23,"value":2},
      |    {"source":31,"target":27,"value":1},
      |    {"source":32,"target":11,"value":1},
      |    {"source":33,"target":11,"value":2},
      |    {"source":33,"target":27,"value":1},
      |    {"source":34,"target":11,"value":3},
      |    {"source":34,"target":29,"value":2},
      |    {"source":35,"target":11,"value":3},
      |    {"source":35,"target":34,"value":3},
      |    {"source":35,"target":29,"value":2},
      |    {"source":36,"target":34,"value":2},
      |    {"source":36,"target":35,"value":2},
      |    {"source":36,"target":11,"value":2},
      |    {"source":36,"target":29,"value":1},
      |    {"source":37,"target":34,"value":2},
      |    {"source":37,"target":35,"value":2},
      |    {"source":37,"target":36,"value":2},
      |    {"source":37,"target":11,"value":2},
      |    {"source":37,"target":29,"value":1},
      |    {"source":38,"target":34,"value":2},
      |    {"source":38,"target":35,"value":2},
      |    {"source":38,"target":36,"value":2},
      |    {"source":38,"target":37,"value":2},
      |    {"source":38,"target":11,"value":2},
      |    {"source":38,"target":29,"value":1},
      |    {"source":39,"target":25,"value":1},
      |    {"source":40,"target":25,"value":1},
      |    {"source":41,"target":24,"value":2},
      |    {"source":41,"target":25,"value":3},
      |    {"source":42,"target":41,"value":2},
      |    {"source":42,"target":25,"value":2},
      |    {"source":42,"target":24,"value":1},
      |    {"source":43,"target":11,"value":3},
      |    {"source":43,"target":26,"value":1},
      |    {"source":43,"target":27,"value":1},
      |    {"source":44,"target":28,"value":3},
      |    {"source":44,"target":11,"value":1},
      |    {"source":45,"target":28,"value":2},
      |    {"source":47,"target":46,"value":1},
      |    {"source":48,"target":47,"value":2},
      |    {"source":48,"target":25,"value":1},
      |    {"source":48,"target":27,"value":1},
      |    {"source":48,"target":11,"value":1},
      |    {"source":49,"target":26,"value":3},
      |    {"source":49,"target":11,"value":2},
      |    {"source":50,"target":49,"value":1},
      |    {"source":50,"target":24,"value":1},
      |    {"source":51,"target":49,"value":9},
      |    {"source":51,"target":26,"value":2},
      |    {"source":51,"target":11,"value":2},
      |    {"source":52,"target":51,"value":1},
      |    {"source":52,"target":39,"value":1},
      |    {"source":53,"target":51,"value":1},
      |    {"source":54,"target":51,"value":2},
      |    {"source":54,"target":49,"value":1},
      |    {"source":54,"target":26,"value":1},
      |    {"source":55,"target":51,"value":6},
      |    {"source":55,"target":49,"value":12},
      |    {"source":55,"target":39,"value":1},
      |    {"source":55,"target":54,"value":1},
      |    {"source":55,"target":26,"value":21},
      |    {"source":55,"target":11,"value":19},
      |    {"source":55,"target":16,"value":1},
      |    {"source":55,"target":25,"value":2},
      |    {"source":55,"target":41,"value":5},
      |    {"source":55,"target":48,"value":4},
      |    {"source":56,"target":49,"value":1},
      |    {"source":56,"target":55,"value":1},
      |    {"source":57,"target":55,"value":1},
      |    {"source":57,"target":41,"value":1},
      |    {"source":57,"target":48,"value":1},
      |    {"source":58,"target":55,"value":7},
      |    {"source":58,"target":48,"value":7},
      |    {"source":58,"target":27,"value":6},
      |    {"source":58,"target":57,"value":1},
      |    {"source":58,"target":11,"value":4},
      |    {"source":59,"target":58,"value":15},
      |    {"source":59,"target":55,"value":5},
      |    {"source":59,"target":48,"value":6},
      |    {"source":59,"target":57,"value":2},
      |    {"source":60,"target":48,"value":1},
      |    {"source":60,"target":58,"value":4},
      |    {"source":60,"target":59,"value":2},
      |    {"source":61,"target":48,"value":2},
      |    {"source":61,"target":58,"value":6},
      |    {"source":61,"target":60,"value":2},
      |    {"source":61,"target":59,"value":5},
      |    {"source":61,"target":57,"value":1},
      |    {"source":61,"target":55,"value":1},
      |    {"source":62,"target":55,"value":9},
      |    {"source":62,"target":58,"value":17},
      |    {"source":62,"target":59,"value":13},
      |    {"source":62,"target":48,"value":7},
      |    {"source":62,"target":57,"value":2},
      |    {"source":62,"target":41,"value":1},
      |    {"source":62,"target":61,"value":6},
      |    {"source":62,"target":60,"value":3},
      |    {"source":63,"target":59,"value":5},
      |    {"source":63,"target":48,"value":5},
      |    {"source":63,"target":62,"value":6},
      |    {"source":63,"target":57,"value":2},
      |    {"source":63,"target":58,"value":4},
      |    {"source":63,"target":61,"value":3},
      |    {"source":63,"target":60,"value":2},
      |    {"source":63,"target":55,"value":1},
      |    {"source":64,"target":55,"value":5},
      |    {"source":64,"target":62,"value":12},
      |    {"source":64,"target":48,"value":5},
      |    {"source":64,"target":63,"value":4},
      |    {"source":64,"target":58,"value":10},
      |    {"source":64,"target":61,"value":6},
      |    {"source":64,"target":60,"value":2},
      |    {"source":64,"target":59,"value":9},
      |    {"source":64,"target":57,"value":1},
      |    {"source":64,"target":11,"value":1},
      |    {"source":65,"target":63,"value":5},
      |    {"source":65,"target":64,"value":7},
      |    {"source":65,"target":48,"value":3},
      |    {"source":65,"target":62,"value":5},
      |    {"source":65,"target":58,"value":5},
      |    {"source":65,"target":61,"value":5},
      |    {"source":65,"target":60,"value":2},
      |    {"source":65,"target":59,"value":5},
      |    {"source":65,"target":57,"value":1},
      |    {"source":65,"target":55,"value":2},
      |    {"source":66,"target":64,"value":3},
      |    {"source":66,"target":58,"value":3},
      |    {"source":66,"target":59,"value":1},
      |    {"source":66,"target":62,"value":2},
      |    {"source":66,"target":65,"value":2},
      |    {"source":66,"target":48,"value":1},
      |    {"source":66,"target":63,"value":1},
      |    {"source":66,"target":61,"value":1},
      |    {"source":66,"target":60,"value":1},
      |    {"source":67,"target":57,"value":3},
      |    {"source":68,"target":25,"value":5},
      |    {"source":68,"target":11,"value":1},
      |    {"source":68,"target":24,"value":1},
      |    {"source":68,"target":27,"value":1},
      |    {"source":68,"target":48,"value":1},
      |    {"source":68,"target":41,"value":1},
      |    {"source":69,"target":25,"value":6},
      |    {"source":69,"target":68,"value":6},
      |    {"source":69,"target":11,"value":1},
      |    {"source":69,"target":24,"value":1},
      |    {"source":69,"target":27,"value":2},
      |    {"source":69,"target":48,"value":1},
      |    {"source":69,"target":41,"value":1},
      |    {"source":70,"target":25,"value":4},
      |    {"source":70,"target":69,"value":4},
      |    {"source":70,"target":68,"value":4},
      |    {"source":70,"target":11,"value":1},
      |    {"source":70,"target":24,"value":1},
      |    {"source":70,"target":27,"value":1},
      |    {"source":70,"target":41,"value":1},
      |    {"source":70,"target":58,"value":1},
      |    {"source":71,"target":27,"value":1},
      |    {"source":71,"target":69,"value":2},
      |    {"source":71,"target":68,"value":2},
      |    {"source":71,"target":70,"value":2},
      |    {"source":71,"target":11,"value":1},
      |    {"source":71,"target":48,"value":1},
      |    {"source":71,"target":41,"value":1},
      |    {"source":71,"target":25,"value":1},
      |    {"source":72,"target":26,"value":2},
      |    {"source":72,"target":27,"value":1},
      |    {"source":72,"target":11,"value":1},
      |    {"source":73,"target":48,"value":2},
      |    {"source":74,"target":48,"value":2},
      |    {"source":74,"target":73,"value":3},
      |    {"source":75,"target":69,"value":3},
      |    {"source":75,"target":68,"value":3},
      |    {"source":75,"target":25,"value":3},
      |    {"source":75,"target":48,"value":1},
      |    {"source":75,"target":41,"value":1},
      |    {"source":75,"target":70,"value":1},
      |    {"source":75,"target":71,"value":1},
      |    {"source":76,"target":64,"value":1},
      |    {"source":76,"target":65,"value":1},
      |    {"source":76,"target":66,"value":1},
      |    {"source":76,"target":63,"value":1},
      |    {"source":76,"target":62,"value":1},
      |    {"source":76,"target":48,"value":1},
      |    {"source":76,"target":58,"value":1}
      |  ]
      |}
    """.stripMargin
}
