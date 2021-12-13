<link rel="stylesheet" href="/css/bootstrap.min.css">
<div class="col-lg-12">
<div class="form-group">
<form method="post" action="/signIn">
    <div class="col-lg-4">
    	<label>Email</label>
    </div>
    <div class="col-lg-4">
    	<input type="email" class="form-control" name="email" placeholder="Enter Email"/>
	</div>
	<div class="col-lg-4">    	
    	<button type="submit"  class="btn btn-primary">Sign In</button>
    </div>	
</form>
<#if error?? >
     <b>An Error Occured, </b>${error}<br>
</#if>
</div>
</div>