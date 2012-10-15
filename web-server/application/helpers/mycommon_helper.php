<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function randomString($n)
{
    $chars = "abcdefghijkmnopqrstuvwxyz023456789"; 
        srand((double)microtime()*1000000);
        $i = 0;
        $pass = '' ;
        while ($i <= $n) {
            $num = rand() % 33;
            $tmp = substr($chars, $num, 1);
            $pass = $pass . $tmp;
            $i++;
        }
        return $pass;
}
?>
