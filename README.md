# スマートウォッチで心拍数測定し，csvとして出力

## インストール方法
AndroidStudioでこのプロジェクトを開いて，実行してください．

## 使い方
「START」ボタンを押すと計測が開始され，「STOP」ボタンを押すと計測終了及びcsvファイルとして保存されます．
使う前に，設定⇒アプリと通知⇒<APP名>⇒アプリの権限⇒センサーをONにしてください．

## 測定したデータの確認方法
測定したデータはsmartwatch本体に保存されています．
AndroidStudioのDevice File Explorerより，"/data/data/jp.aoyama.a5817010.heartbeat/<取得した日付>.csv"に保存されているかと思います．
