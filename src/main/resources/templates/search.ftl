<link href="/css/bootstrap.min.css" rel="stylesheet">
<#if emailAddress?? >
<form method="get" action="/signOut">
	</br><div class="col-lg-8">Welcome, <b><i>${emailAddress}</i></b>&nbsp Customer Type: <b>${customerType}</b>&nbsp<button type="submit" class="btn btn-primary">Sign out</button><br></div>
</form>
<form method="post" action="/search">
<div class="col-lg-12">
    <div class="col-lg-4">
    	<label>Enter Surname</label> <input type="text"  class="form-control" name="surname" placeholder="Surname"/>
    </div>
    <div class="col-lg-4">
    	<label>Enter Postcode</label> <input type="text"  class="form-control" name="postcode" placeholder="Postcode"/>
	</div>    	
    <input type="hidden"  class="form-control" name="email" value =  ${emailAddress}></br>
    <div class="col-lg-2">
    <button type="submit" class="btn btn-primary">Search</button>
    </div>
    </div>
</form>
<div>
</#if>
 <#if records?? >
 <div class="col-sm-4">
 	<table border="1" cellspacing="0" cellpadding="1">
    <tr class="tableHeader">
        <th>Name</th>
        <th>Telephone</th>
        <th>Address</th>
        <th>Source Types</th>
    </tr>
    <#foreach record in records>      	                                            
        <tr class="tableBody">
            <td>${record.name}</td>
            <td>${record.telephone}</td>
         	<td>${record.address}</td>
         	<td>${record.sourceTypes}</td> 
        </tr>
    </#foreach>                             
</table>
</div>
<#elseif error??>
	<b>NO RECORDS AVAILABLE!</b>
 </#if>