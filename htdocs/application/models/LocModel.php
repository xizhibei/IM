<?php

/**
 * LocationModel
 *  
 * @author X
 * @version 
 */
require_once 'Zend/Db/Table/Abstract.php';

class LocModel extends Zend_Db_Table_Abstract {

    /**
     * The default table name
     */
    protected $_name = 'location';

    /**
     *
     * @param unknown_type $all
     *        	all data include geo and uid
     * @param unknown_type $n
     *        	cluster number
     */
    public function kmeans($all, $n) {
        $size = count($all);
        $cluster = array();
        $step = intval($size / $n);
        $tmp = 0;
        for ($i = 0; $i < $n; $i++) {
            $tmp = rand($tmp, $tmp + $step);
            $cluster [$i] = array(
                'oldlat' => 0,
                'oldlng' => 0,
                'lat' => $all [$tmp] ['lat'],
                'lng' => $all [$tmp] ['lng'],
                'sub' => array()
            );
        }
        $iter = 0;
        while (true) {
            $iter++;
            // cluster
            for ($i = 0; $i < $size; $i++) {
                $min_c = 0;
                $min_d = 10000;
                for ($j = 0; $j < $n; $j++) {
                    $d = $this->GetDistance($all [$i] ['lat'], $all [$i] ['lng'], $cluster [$j] ['lat'], $cluster [$j] ['lng']);
                    if ($d < $min_d) {
                        $min_c = $j;
                        $min_d = $d;
                    }
                }
                array_push($cluster [$min_c] ['sub'], $i);
            }

            // update center
            for ($i = 0; $i < $n; $i++) {
                $latsum = 0;
                $lngsum = 0;

                foreach ($cluster [$i] ['sub'] as $c) {
                    $latsum += $all [$c] ['lat'];
                    $lngsum += $all [$c] ['lng'];
                }

                $count = count($cluster [$i] ['sub']);

                $cluster [$i] ['oldlat'] = $cluster [$i] ['lat'];
                $cluster [$i] ['oldlng'] = $cluster [$i] ['lng'];
                if ($count != 0) {
                    $cluster [$i] ['lat'] = $latsum / $count;
                    $cluster [$i] ['lng'] = $lngsum / $count;
                }
            }

            //var_dump($cluster);
            // is enough
            $isEnough = false;
            for ($i = 0; $i < $n; $i++) {
                if ($this->GetDistance($cluster [$i] ['oldlat'], $cluster [$i] ['oldlng'], $cluster [$i] ['lat'], $cluster [$i] ['lng']) < 1) {
                    $isEnough = true;
                } else {
                    $isEnough = false;
                    break;
                }
            }

            if ($isEnough)
                break;

            for ($i = 0; $i < $n; $i++)
                $cluster [$i] ['sub'] = array();
        }
        //echo $iter;
        return $cluster;
    }

    public function cluster($num, $ltLat = null, $ltLng = null, $rbLat = null, $rbLng = null, $gender = null, $agelevel = null, $updatetime = null) {
        $where = "";
        if ($ltLat != null) {
            $where .= " $ltLat > latitude and $ltLng < longitude and $rbLat < latitude and $rbLng > longitude ";
        }
        $tmp = "";
        if ($gender != null) {
            if ($gender == "Male")
                $tmp .= " sex = 1 ";
            else if ($gender == "Female")
                $tmp .= " sex = 2 ";
        }
        if ($tmp != "")
            $where .= " and " . $tmp;
        $tmp = "";
        if ($agelevel != null) {
            if ($agelevel == "1") {//<20
                $tmp .= "birthday < ";
            } else if ($agelevel == "2") {
                $tmp .= "birthday";
            } else if ($agelevel == "3") {
                $tmp .= "birthday";
            } else if ($agelevel == "4") {
                $tmp .= "birthday";
            }
        }
        if ($tmp != "")
            $where .= " and " . $tmp;
        $tmp = "";
        if ($updatetime != null) {
            if ($updatetime == "15m") {
                $tmp .= " locupdatetime > " . (time() - 900);
            } else if ($updatetime == "1h") {
                $tmp .= " locupdatetime > " . (time() - 3600);
            } else if ($updatetime == "12h") {
                $tmp .= " locupdatetime > " . (time() - 43200);
            } else if ($updatetime == "1d") {
                $tmp .= " locupdatetime > " . (time() - 86400);
            }else{
                $tmp .= " locupdatetime <> 0 ";
            }
        } else {
            $tmp .= " locupdatetime <> 0 ";
        }
        if ($tmp != "") {
            if ($where != "")
                $where .= " and " . $tmp;
            else
                $where = $tmp;
        }
        //echo $where;
        $data = $this->_db->fetchAll("select latitude as lat,longitude as lng,uid from user where $where");
        if(count($data) == 0)
            return null;
        foreach ($data as &$d) {
            $d['lat'] = $d['lat'] / 1e6;
            $d['lng'] = $d['lng'] / 1e6;
        }
        //var_dump($data);
        
        return ( $this->kmeans($data, $num) );
    }

    public function distance($g1, $g2) {
        return $this->GetDistance($g1 ['lat'], $g1 ['lng'], $g2 ['lat'], $g2 ['lng']);
    }

    public function GetDistance($lat1, $lng1, $lat2, $lng2) {
        $EARTH_RADIUS = 6378.137;
        $radLat1 = deg2rad($lat1);
        $radLat2 = deg2rad($lat2);
        $a = $radLat1 - $radLat2;
        $b = deg2rad($lng1) - deg2rad($lng2);
        $s = 2 * asin(sqrt(pow(sin($a / 2), 2) + cos($radLat1) * cos($radLat2) * pow(sin($b / 2), 2)));
        $s = $s * $EARTH_RADIUS;
        $s = round($s * 10000) / 10000;
        return $s;
    }

    public function _GetDistance($lat1, $lng1, $lat2, $lng2) {
        $r = 6371.137;
        $dlat = deg2rad($lat2 - $lat1);
        $dlng = deg2rad($lng2 - $lng1);

        $a = pow(sin($dlat / 2), 2) + cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * pow(sin($dlng / 2), 2);

        $c = 2 * atan2(sqrt($a), sqrt(1 - $a));

        return $r * $c;
    }

    public function GetNewestLocation($uid) {
        $result = $this->_db->fetchRow("select * from location where uid = $uid order by updatetime desc limit 0,1");
        return $result;
    }

    public function UpdateNewLocation($uid, $latitude, $longitude) {
        $time = time();
        $insertData = array(
            'latitude' => $latitude,
            'longitude' => $longitude,
            'updatetime' => $time,
            'uid' => $uid
        );
        $this->insert($insertData);
        $this->_db->update("user", array(
            'latitude' => $latitude,
            'longitude' => $longitude,
            'locupdatetime' => $time
                ), "uid = $uid");
    }

    public function NearbyPeople($uid, $latitude, $longitude, $max_dis = 1) {
        $EARTH_RADIUS = 6378.137;
        $dlng = 2 * asin(sin($max_dis / (2 * $EARTH_RADIUS)) / cos(deg2rad($latitude / 1e6)));
        $dlng = rad2deg($dlng);

        $dlat = $max_dis / $EARTH_RADIUS;
        $dlat = rad2deg($dlat);

        $squre = array(
            'lt' => array(
                'lat' => $latitude + $dlat * 1e6,
                'lng' => $longitude - $dlng * 1e6
            ),
            // 'rt' => array('lat' => $latitude + $dlat, 'lng' => $longitude
            // + $dlng),
            // 'lb' => array('lat' => $latitude - $dlat, 'lng' => $longitude
            // - $dlng),
            'rb' => array(
                'lat' => $latitude - $dlat * 1e6,
                'lng' => $longitude + $dlng * 1e6
            )
        );
        // echo $this->GetDistance($squre['lt']['lat'] /
        // 10e6,$squre['lt']['lng'] / 10e6,$squre['rb']['lat'] /
        // 10e6,$squre['rb']['lng'] / 10e6);
        // var_dump($squre);

        $sql = "select * from user where {$squre['lt']['lat']} > latitude and 
        {$squre['lt']['lng']} < longitude and {$squre['rb']['lat']} < latitude and 
        {$squre['rb']['lng']} > longitude";

        $result = $this->_db->fetchAll($sql);
        foreach ($result as &$p) {
            if ($p ['sex'] == 0) {
                $p ['sex'] = "Unknow";
            } else if ($p ['sex'] == 1) {
                $p ['sex'] = "Male";
            } else
                $p ['sex'] = "Female";

            $h = time() - $p ['locupdatetime'];
            if ($h / 86400 >= 1)
                $p ['updatetime'] = intval($h / 86400) . "天前";
            else if ($h / 3600 >= 1)
                $p ['updatetime'] = intval($h / 3600) . "小时前";
            else
                $p ['updatetime'] = intval($h / 60) . "分钟前";

            $p ['regdate'] = date("Y-m-d", $p ['regdate']);
            $p ['age'] = intval((time() - $p['birthday']) / (365.25 * 86400));
            $p ['distance'] = $this->GetDistance($latitude / 1e6,$longitude /1e6,$p['latitude'] / 1e6,$p['longitude'] / 1e6);
        }
        return $result;
    }

    public function NearbySeller($uid) {
        
    }

}
