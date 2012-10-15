<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
class Login_ci extends CI_Controller
{
    
    function index()
    {
        $this->load->helper('mycommon');
        if(array_key_exists('id', $_GET) && array_key_exists('pass', $_GET))
        {
            header('Content-type: application/json');
            $this->db->where('id', $_GET['id']);
            $this->db->where('pass', $_GET['pass']);
            $query = $this->db->get('users');
            if($query->num_rows()==1)
            {
                $res = $query->result();
                echo json_encode($res[0]);
                return;
            }
        }
        
        $query = $this->db->get('users');
        $data['id'] = $query->num_rows()+1;
        

        $data['pass'] = randomString(18);
        $data['verified'] = 0;
        $data['email'] = 'sample@abc.com';
        $data['name'] = 'Anonymous';
        $data['address'] = '';
        $data['detail'] = '';
        $this->db->insert('users', $data);
        echo json_encode($data);
    }
    
    function update()
    {
        if(array_key_exists('id', $_GET) && array_key_exists('pass', $_GET))
        {
            $this->db->where('id', $_GET['id']);
            $this->db->where('pass', $_GET['pass']);
            $query = $this->db->update('users', $_GET);
            echo json_encode(array('result'=>1));
            return;
        }
        echo json_encode(array('result'=>0));
    }
    
}
?>
