/**
 * Created by Canon on 2015-04-20.
 */
function drawSimple() {

    var width = 800,
        height = 800;

    var color = d3.scale.category20();

    var force = d3.layout.force()
        .charge(-120)
        .linkDistance(40)
        .size([width, height]);

    var svg = d3.select(".jumbotron").append("svg")
        .attr("width", width)
        .attr("height", height);

    svg.append("defs").selectAll("marker")
            .data(["suit", "licensing", "resolved"])
          .enter().append("marker")
            .attr("id", function(d) { return d; })
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", 15)
            .attr("refY", -1.5)
            .attr("markerWidth", 6)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
        .append("path")
        .attr("d", "M0,-5L10,0L0,5");

    d3.json("./static/data.json", function (error, graph) {
        force
            .nodes(graph.nodes)
            .links(graph.links)
            .start();


        var path = svg.selectAll("path")
            .data(graph.links)
            .enter().append("path")
            .attr("class", "link suit")
            .attr("marker-end", "url(#suit)")
            .style("stroke-width", function (d) {
                return Math.sqrt(d.value);
            });

        var node = svg.selectAll(".node")
            .data(graph.nodes)
            .enter().append("circle")
            .attr("class", "node")
            .attr("r", 5)
            .style("fill", function (d) {
                return color(d.group+1);
            })
            .call(force.drag);

        node.append("title")
            .text(function (d) {
                return d.name;
            });

        force.on("tick", tick);


        function tick() {
          path.attr("d", linkArc);
          node.attr("transform", transform);
          //text.attr("transform", transform);
        }

        function linkArc(d) {
          var dx = d.target.x - d.source.x,
              dy = d.target.y - d.source.y,
              dr = Math.sqrt(dx * dx + dy * dy);
          return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
        }

        function transform(d) {
          return "translate(" + d.x + "," + d.y + ")";
        }
    });

}

function clearGraph() {
    d3.selectAll("svg").remove();
}
