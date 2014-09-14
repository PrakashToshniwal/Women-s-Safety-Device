<H1>Server_Handler<H1>
<?php
error_reporting(~E_NOTICE);
set_time_limit (0);
 
$address = gethostbyname(trim(exec('hostname')));                            //Server Address for socket binding
$port = 81;                                                                                                            //Server Port for socket binding
$max_clients = 10;
//Code to create socket for TCP Communication
if(!($sock = socket_create(AF_INET, SOCK_STREAM, 0)))
{
    $errorcode = socket_last_error();
    $errormsg = socket_strerror($errorcode);
     
    die("Couldn't create socket: [$errorcode] $errormsg \n");
}
 
echo "Socket created  on :".$address."\n";
 
// Bind the server address with specified port, if available
if( !socket_bind($sock, $address , $port) )
{
    $errorcode = socket_last_error();
    $errormsg = socket_strerror($errorcode);
     
    die("Could not bind socket : [$errorcode] $errormsg \n");
}
 
echo "Socket bind OK \n";

//Listen for clients for communication 
if(!socket_listen ($sock , 10))
{
    $errorcode = socket_last_error();
    $errormsg = socket_strerror($errorcode);
     
    die("Could not listen on socket : [$errorcode] $errormsg \n");
}
 
echo "Socket listen OK \n";
 
echo "Waiting for incoming connections... \n";
 
//array of client sockets
$client_socks = array();
 
//array of sockets to read
$read = array();
 
//start loop to listen for incoming connections and process existing connections
while (true) 
{
    //prepare array of readable client sockets
    $read = array();
     
    //first socket is the master socket
    $read[0] = $sock;
     
    //Adding the existing client sockets
    for ($i = 0; $i < $max_clients; $i++)
    {
        if($client_socks[$i] != null)
        {
            $read[$i+1] = $client_socks[$i];
        }
    }
     
    //Call select - blocking call
    if(socket_select($read , $write , $except , null) === false)
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);
     
        die("Could not listen on socket : [$errorcode] $errormsg \n");
    }
     
    //If ready contains the master socket, then a new connection has come in
    if (in_array($sock, $read)) 
    {
        for ($i = 0; $i < $max_clients; $i++)
        {
            if ($client_socks[$i] == null) 
            {
                $client_socks[$i] = socket_accept($sock);
                 
                //display information about the client who is connected
                if(socket_getpeername($client_socks[$i], $address, $port))
                {
                    echo "Client $address : $port is now connected to us. \n";
                }
                 
                //Send Welcome message to client
                $message = "Server connected, Enter a message \n";
                socket_write($client_socks[$i] , $message);
                break;
            }
        }
    }
 
    //check each client if they send any data
    for ($i = 0; $i < $max_clients; $i++)
    {
        if (in_array($client_socks[$i] , $read))
        {
            if(false === ($input = socket_read($client_socks[$i] , 1024, PHP_NORMAL_READ)))
            {
                unset($client_socks[$i]);
                continue;
            }
             $input = trim($input);
            
 
            $n = trim($input);
 
            $output = explode(",", $input);
            $latitude = trim($output[0]);
            $longitude = trim($output[1]);
            echo "Location of client:- \nLatitude: ".$latitude.", Longitude: ". $longitude."\n";
            
            
            //get location
            $address_data = geo2address($latitude, $longitude);
            if(!$address_data)
            {
                socket_write($client_socks[$i] , "Not able to validate GPS coordinates\n");
                continue;
            }
             
            //send location to specified mobile number
            if(true === send_sms('7415470017', 'Q4524K', '7415470017', $address_data))
            {
               echo "Address Location:".$address_data." sent to specified mobile numbers\n";
            }
        }
    }
}

//Code to get valid address from GPS coordinates using Google Maps API
function geo2address($lat,$long) {
    $url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$long&sensor=false";
    $curlData=file_get_contents($url);
    $address = json_decode($curlData);
    $a=$address->results[0];
    return($a->formatted_address);
}

//Way2SMS API to send address to mobile Nos.
function send_sms($userID,$userPWD,$recerverNO,$message) {
if (!function_exists('curl_init')) {
echo 'Error : Curl library not installed';
return FALSE;
}
$message_urlencode=rawurlencode($message);
if (strlen($message) > 140) {
$message = substr($message, 0, 139);
}

$cookie_file_path = './cookie.txt';
$temp_file = './temporary.txt';
$user_agent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";

// LOGIN TO WAY2SMS

$url = "http://site24.way2sms.com/content/Login1.action";
$parameters = array("username"=>"$userID","password"=>"$userPWD","button"=>"Login");

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POST, count($parameters));
curl_setopt($ch, CURLOPT_POSTFIELDS, $parameters);
curl_setopt($ch, CURLOPT_HEADER, TRUE);
curl_setopt($ch, CURLOPT_RETURNTRANSFER,TRUE);
curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie_file_path);
curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie_file_path);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
curl_setopt($ch, CURLOPT_USERAGENT, $user_agent);
curl_setopt($ch, CURLOPT_NOBODY, FALSE);
$result = curl_exec ($ch);
curl_close ($ch);

// SAVE LOGOUT URL

file_put_contents($temp_file,$result);
$result = "";
$logout_url = "";
$file = fopen($temp_file,"r");
$line = "";
$cond = TRUE;
while ($cond == TRUE) {
$line = fgets($file);
if ($line === FALSE) { // EOF
$cond = FALSE;
} else {
$pos = strpos( $line, " window.location=");
if ($pos === FALSE ) {
$line = "";
} else { // URL FOUND
$cond = FALSE;
$logout_url = substr($line,-25);
$logout_url = substr($logout_url,0,21);
}
}
}
fclose($file);

// SAVE SESSION ID

$file = fopen($cookie_file_path,"r");
$line = "";
$cond = TRUE;
while ($cond == TRUE) {
$line = fgets($file);
if ($line === FALSE) { // EOF
$cond = FALSE;
} else {
$pos = strpos( $line, "JSESSIONID");
if ($pos === FALSE ) {
$line = "";
} else { // SESSION ID FOUND
$cond = FALSE;
$id = substr($line,$pos+15);
}
}
}
fclose($file);

// SEND SMS

$url = "http://site24.way2sms.com/smstoss.action?Token=" . $id;
$parameters = array("button"=>"Send SMS","mobile"=>"$recerverNO","message"=>"$message");

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POST, count($parameters));
curl_setopt($ch, CURLOPT_POSTFIELDS, $parameters);
curl_setopt($ch, CURLOPT_HEADER, TRUE);
curl_setopt($ch, CURLOPT_RETURNTRANSFER,TRUE);
curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie_file_path);
curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie_file_path);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
curl_setopt($ch, CURLOPT_USERAGENT, $user_agent);
curl_setopt($ch, CURLOPT_NOBODY, FALSE);
$result = curl_exec ($ch);
curl_close ($ch);

// LOGOUT WAY2SMS

$url = "site24.way2sms.com/" . $logout_url;

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HEADER, TRUE);
curl_setopt($ch, CURLOPT_RETURNTRANSFER,TRUE);
curl_setopt($ch, CURLOPT_COOKIEJAR, $cookie_file_path);
curl_setopt($ch, CURLOPT_COOKIEFILE, $cookie_file_path);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
curl_setopt($ch, CURLOPT_USERAGENT, $user_agent);
curl_setopt($ch, CURLOPT_NOBODY, FALSE);
$result = curl_exec ($ch);
curl_close ($ch);

// DELETE TEMP FILES

unlink($cookie_file_path);
unlink($temp_file);

return TRUE;

}
?>
