<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
class Markers extends CI_Controller
{
    function index()
    {
        header('Content-type: application/json');
        $query = $this->db->query("SELECT * from pics WHERE latitude >=".$_GET['lat_min'] ." AND latitude<=".$_GET['lat_max']." AND longitude >=".$_GET['long_min']." AND longitude<=".$_GET['long_max']);
        $res=array('count'=>count($query->result()), 'result'=>$query->result());
        
        echo json_encode($res);
        return;
    }
}
?>
