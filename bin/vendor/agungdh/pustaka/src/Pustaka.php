<?php 
namespace agungdh;

class Pustaka {
	public static function romawi($n){
		$hasil = "";
		$iromawi = ["","I","II","III","IV","V","VI","VII","VIII","IX","X",
			20=>"XX",30=>"XXX",40=>"XL",50=>"L",60=>"LX",70=>"LXX",80=>"LXXX",
			90=>"XC",100=>"C",200=>"CC",300=>"CCC",400=>"CD",500=>"D",
			600=>"DC",700=>"DCC",800=>"DCCC",900=>"CM",1000=>"M",
			2000=>"MM",3000=>"MMM"];

		if(array_key_exists($n,$iromawi)){
		$hasil = $iromawi[$n];
		}elseif($n >= 11 && $n <= 99){
		$i = $n % 10;
		$hasil = $iromawi[$n-$i] . self::romawi($n % 10);
		}elseif($n >= 101 && $n <= 999){
		$i = $n % 100;
		$hasil = $iromawi[$n-$i] . self::romawi($n % 100);
		}else{
		$i = $n % 1000;
		$hasil = $iromawi[$n-$i] . self::romawi($n % 1000);
		}

		return $hasil;
	}

	public static function decimalRand($iMin, $iMax, $fSteps = 0.5)
	{
	    $a = range($iMin, $iMax, $fSteps);

	    return $a[mt_rand(0, count($a)-1)];
	}
	
	public static function dropTableView($host, $user, $pass, $db)
	{
		$mysqli = new \mysqli($host, $user, $pass, $db);
		
		$mysqli->query('SET foreign_key_checks = 0');

		if ($tables = $mysqli->query("SHOW FULL TABLES IN " . $db . " WHERE TABLE_TYPE LIKE 'BASE TABLE'"))
		{
		    while($row = $tables->fetch_array(MYSQLI_NUM))
		    {
		        $mysqli->query('DROP TABLE IF EXISTS '.$row[0]);
		    }
		}

		if ($views = $mysqli->query("SHOW FULL TABLES IN " . $db . " WHERE TABLE_TYPE LIKE 'VIEW'"))
		{
		    while($row = $views->fetch_array(MYSQLI_NUM))
		    {
		        $mysqli->query('DROP VIEW IF EXISTS '.$row[0]);
		    }
		}

		$mysqli->query('SET foreign_key_checks = 1');
		
		$mysqli->close();
	}
	
	public static function arrangeForSelectCollective($raw, $params)
    {
        $selectValue = $params[0];
        
        unset($params[0]);
        $params = array_values($params);

        $datas = [];
        
        foreach ($raw as $value) {
            $display = '';
            foreach ($params as $forDisplay) {
                if (strpos($forDisplay, '__') !== false) {
                    $toDisplay = str_replace('__', '', $forDisplay);
                    $display .= $value->$toDisplay;
                } else {
                    $display .= $forDisplay;
                }
            }
            $datas[$value->$selectValue] = $display;
        }

        return $datas;
    }

	public static function parseTanggalIndo($tanggal)
	{
		return date_format(date_create($tanggal),"Y-m-d");
	}

	public static function parseTanggalWaktuIndo($tanggalWaktu)
	{
		return date_format(date_create($tanggalWaktu),"Y-m-d H:i:s");
	}

	public static function rupiah($angka, $rp = true, $koma = true) {
		if ($koma == true) {
			$dua = 2;
		} else {
			$dua = 0;
		}

		$hasil_rupiah = number_format($angka,$dua,',','.');

		if ($rp == true) {
			$hasil_rupiah = 'Rp' . $hasil_rupiah;
		}

		return $hasil_rupiah;
	}

	public static function ribuan($angka) {
		return number_format($angka,0,',','.');
	}

	public static function tanggalIndo($tanggal) {
		return date("d-m-Y", strtotime($tanggal));
	}	

	public static function tanggalWaktuIndo($tanggalWaktu) {
		return date("d-m-Y H:i:s", strtotime($tanggalWaktu));
	}	

	public static function tanggalIndoString($tanggal){
		$bulan = array (
			1 =>   'Januari',
			'Februari',
			'Maret',
			'April',
			'Mei',
			'Juni',
			'Juli',
			'Agustus',
			'September',
			'Oktober',
			'November',
			'Desember'
		);
		$pecahkan = explode('-', $tanggal);
	 
		return $pecahkan[2] . ' ' . $bulan[ (int)$pecahkan[1] ] . ' ' . $pecahkan[0];
	}
	
	public static function tanggalIndoStringBulanTahun($bulanTahun){
		$bulan = array (
			1 =>   'Januari',
			'Februari',
			'Maret',
			'April',
			'Mei',
			'Juni',
			'Juli',
			'Agustus',
			'September',
			'Oktober',
			'November',
			'Desember'
		);
		$pecahkan = explode('-', $bulanTahun);
	 
		return $bulan[ (int)$pecahkan[0] ] . ' ' . $pecahkan[1];
	}

	public static function tanggalWaktuIndoString($tanggalWaktu){
		$bulan = array (
			1 =>   'Januari',
			'Februari',
			'Maret',
			'April',
			'Mei',
			'Juni',
			'Juli',
			'Agustus',
			'September',
			'Oktober',
			'November',
			'Desember'
		);
		$tanggal = explode(' ', $tanggalWaktu)[0];
		$waktu = explode(' ', $tanggalWaktu)[1];
		$pecahkan = explode('-', $tanggal);
	 
		return $pecahkan[2] . ' ' . $bulan[ (int)$pecahkan[1] ] . ' ' . $pecahkan[0] . ' ' . $waktu;
	}

	public static function penyebut($nilai) {
		$nilai = abs($nilai);
		$huruf = array("", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas");
		$temp = "";
		if ($nilai < 12) {
			$temp = " ". $huruf[$nilai];
		} else if ($nilai <20) {
			$temp = self::penyebut($nilai - 10). " belas";
		} else if ($nilai < 100) {
			$temp = self::penyebut($nilai/10)." puluh". self::penyebut($nilai % 10);
		} else if ($nilai < 200) {
			$temp = " seratus" . self::penyebut($nilai - 100);
		} else if ($nilai < 1000) {
			$temp = self::penyebut($nilai/100) . " ratus" . self::penyebut($nilai % 100);
		} else if ($nilai < 2000) {
			$temp = " seribu" . self::penyebut($nilai - 1000);
		} else if ($nilai < 1000000) {
			$temp = self::penyebut($nilai/1000) . " ribu" . self::penyebut($nilai % 1000);
		} else if ($nilai < 1000000000) {
			$temp = self::penyebut($nilai/1000000) . " juta" . self::penyebut($nilai % 1000000);
		} else if ($nilai < 1000000000000) {
			$temp = self::penyebut($nilai/1000000000) . " milyar" . self::penyebut(fmod($nilai,1000000000));
		} else if ($nilai < 1000000000000000) {
			$temp = self::penyebut($nilai/1000000000000) . " trilyun" . self::penyebut(fmod($nilai,1000000000000));
		}     
		return $temp;
	}
 
	public static function terbilang($nilai) {
		if($nilai<0) {
			$hasil = "minus ". trim(self::penyebut($nilai));
		} else {
			$hasil = trim(self::penyebut($nilai));
		}     		
		return $hasil;
	}
}