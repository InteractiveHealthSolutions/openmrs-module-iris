<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<style>
<!--
table.datatableiva {
	border: 1px gray solid;
	border-spacing: 0px;
	border-collapse: collapse;
	margin: 2px;
	width: 100%;
}

table.datatableiva td {
	border: 1px #F2F2F2 solid;
	padding: 1px;
	font-size: 12px;
}

table.datatableiva th {
	border: 1px #F2F2F2 solid;
	padding: 1px;
}
.heading{
	font-size: large;
	font-weight: bold;
	color: black;
}

a:HOVER {
	cursor: pointer;
}
-->
</style>
<script type="text/javascript">

function resetDefaultConcept(){
	if(confirm("This would rest ALL Concepts to default concepts of IRIS application. This would override existing data and is not reversible.")){
		window.location="${pageContext.request.contextPath}/module/iris/outputmappings/generateOutputMappingConcepts.form";
	}
}

function showIvaMappingDetails(objid){
	var detailstr = "<html><head><title>IRIS OUTPUT Mapping ("+objid+")</title></head>"+
					"<style>"+
					"table.datatableiva { border: 1px gray solid; border-collapse: collapse; }"+
					"table.datatableiva td { border: 1px #F2F2F2 solid; }"+
					"table.datatableiva th { border: 1px #F2F2F2 solid; text-align: left}"+
					".heading{ font-size: large; font-weight: bold; color: black; background-color: silver;}"+
					"</style><body>"+
					"<div class='heading'>IRIS OUTPUT Mapping</div>"+
					"<table class='datatableiva'>";
	<c:forEach items="${outputmappings}" var="item" varStatus="loop"> 
    	if('${item.irisOutputMappingId}' == (''+objid)){
    		detailstr += 
    			"<tr><th>Mapping ID </th><td>${item.irisOutputMappingId}</td></tr>"+
    			"<tr><th>Variable name</th><td>${item.irisOutputVariable}</td></tr>"+
    		   	"<tr><th>Index</th><td>${item.irisOutputIndex}</td></tr>"+
    		   	"<tr><th>Concept ID</th><td>${item.concept.conceptId}</td></tr>"+
    		   	"<tr><th>Concept</th><td>${item.concept.name}</td></tr>"+
    		   	"<tr><th>Observation</th><td>${item.observation}</td></tr>"+
    		   	"<tr><th>Creator</th><td>${item.creator.username}</td></tr>"+
    		   	"<tr><th>Date Created</th><td>${item.dateCreated}</td></tr>"+
    		   	"<tr><th>Changed By</th><td>${item.changedBy.username}</td></tr>"+
    		   	"<tr><th>Date Changed</th><td>${item.dateChanged}</td></tr>"+
    		   	"<tr><th>Voided</th><td>${item.voided}</td></tr>"+
    		   	"<tr><th>Date Voided</th><td>${item.dateVoided}</td></tr>"+
    		   	"<tr><th>Voided By</th><td>${item.voidedBy.username}</td></tr>"+
    		   	"<tr><th>Void Reason</th><td>${item.voidReason}</td></tr>"+
    		   	"<tr><th>Description</th> <td>${item.description}</td></tr>";
    	}
  	</c:forEach>  
  	detailstr += "</table></body></html>";
  	var win;

	win=window.open('','IVA OUTPUT MAPPING-ID:'+objid,'width=600,height=700,resizable=no,toolbar=no,location=no,scrollbars=yes,directories=no,status=no,menubar=no,copyhistory=no');
	win.document.write(detailstr);
	win.focus();
}

</script>
<div class="heading">IRIS Mappings for OUTPUT FILE</div>
<div id="detailsDiv" class="">${message}</div>
<div style="float: right;"><a onclick="resetDefaultConcept();" >Reset default concepts</a></div>
<table class="datatableiva">
  <tr>
   <th></th>
   <th>Mapping ID</th>
   <th>Variable name</th>
   <th>Index</th>
   <th>Concept ID</th>
   <th>Concept</th>
   <th>Observation</th>
   <!-- <th>Creator</th> -->
   <!-- <th>Date Created</th> -->
   <!-- <th>Changed By</th>
   <th>Date Changed</th> -->
   <th>Voided</th>
   <!-- <th>Date Voided</th> -->
   <!-- <th>Voided By</th> -->
   <th>Void Reason</th>
   <th>Description</th>
  </tr>
  <c:forEach var="oml" items="${outputmappings}">
      <tr>
      	<td><a onclick="showIvaMappingDetails('${oml.irisOutputMappingId}');">details</a></td>
        <td>${oml.irisOutputMappingId}</td>
        <td>${oml.irisOutputVariable}</td>
        <td>${oml.irisOutputIndex}</td>
       <td>${oml.concept.conceptId}</td>
        <td style="text-transform: capitalize">${fn:toLowerCase(oml.concept.name)}</td>
        <td>${oml.observation}</td>
       <!--  <td></td> -->
        <%-- <td>${oml.dateCreated}</td> --%>
       <%--  <td></td>
        <td>${oml.dateChanged}</td> --%>
        <td>${oml.voided}</td>
       <%--  <td>${oml.dateVoided}</td> --%>
        <!-- <td></td> -->
        <td>${oml.voidReason}</td>
        <td>${oml.description}</td>
      </tr>		
  </c:forEach>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>
