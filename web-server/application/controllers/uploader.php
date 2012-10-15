<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
class Uploader extends CI_Controller
{
    
    function index()
    {
        if(array_key_exists('id', $_GET) && array_key_exists('pass', $_GET))
        {
            header('Content-type: application/json');
            $this->db->where('id', $_GET['id']);
            $this->db->where('pass', $_GET['pass']);
            $query = $this->db->get('users');
            if($query->num_rows()==1)
            {
                $this->db->where('id', $_GET['id']);
                $this->db->where('pass', $_GET['pass']);
                $this->load->helper('mycommon');
                $secret = randomString(5);
                $_GET['picsecret'] = $secret;
                $query = $this->db->update('users', $_GET);
                echo $secret;
                return;
            }
        }
        echo "0";
        return;
    }
    function storefile()
    {
        
        $vals = explode(".", $_FILES['uploadedfile']['name']);
        echo var_dump($vals);
        $this->db->where('id', $vals[0]);
        $this->db->where('picsecret', $vals[1]);
        $query = $this->db->get('users');
        if($query->num_rows()!=1)
        {
            echo "Failed to validate request".$query->num_rows();
            return;
        }
        
        if (1/*($_FILES["uploadedfile"]["type"] == "image/gif")
            || ($_FILES["uploadedfile"]["type"] == "image/jpeg")
            || ($_FILES["uploadedfile"]["type"] == "image/pjpeg")*/)
        {
            if ($_FILES["uploadedfile"]["error"] > 0 )
            {
                echo $_FILES["uploadedfile"]["error"];
                return;
            }
            else if($_FILES["uploadedfile"]["size"] > 2000000)
            {
                echo "File Size exceded 2MB";
                return;
            }
            if(count($vals)<2)
            {
                echo "Invalied file.";
                return;
            }
            $num = 0;
            while (file_exists("application/uploads/".$vals[0]."_".$num.".".$vals[count($vals)-1])) $num=$num+1;
            $_GET['filename'] = $vals[0]."_".$num.".".$vals[count($vals)-1];
            $_GET['uploaddate'] = date("Y-m-d"); 
            $this->db->insert('pics', $_GET);
            move_uploaded_file($_FILES["uploadedfile"]["tmp_name"],
            "application/uploads/" . $vals[0]."_".$num.".".$vals[count($vals)-1]);
            echo "Upload Successful";
            echo "GET";
            var_dump($_GET);
        }
        else
        {
            echo "Invalid file Type: ".$_FILES["uploadedfile"]["type"]."XXX";
        }
    }
}
?>
