<link rel="stylesheet" href="/css/bootstrap.min.css">
</br>
<div class="col-lg-12">
<div class="form-group">
<form method="post" action="/signIn">
    <div class="col-lg-4">
    	<label>Enter Customer Email</label>
    </div>
    <div class="col-lg-4">
    	<input type="email" class="form-control" name="email" placeholder="abc.123@xyz.com"/>
	</div></br>
	<div class="col-lg-4">    	
    	<button type="submit"  class="btn btn-primary">Sign In</button>
    </div>	
</form>
<#if error?? >
     <b>${error}</b><br>
</#if>
</div>
</div>