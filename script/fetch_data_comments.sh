DEFAULT_END=5


get_current_date() {
        echo $(date +%F)
}

get_id() {
        id=$(tail -1 /home/baurzhan87/script/file_database.txt)
        expr $id + 1 >> /home/baurzhan87/script/file_database.txt
        expr $id + 0
}

write_to_kafka() {
        echo $1| kafka-console-producer.sh --topic comments  --bootstrap-server kafka:9092 
}
#d=$(get_current_date)
#id=$(get_id)

#echo $d
#echo $id
#echo $response
for (( i=1; i<=$DEFAULT_END; i++ ))
do
        response=$(curl -s https://jsonplaceholder.typicode.com/comments/$i) 
        json=$(echo $response | jq --arg idFromFile $(get_id) '. + {idFromFile: $idFromFile|tonumber}')
        json_final=$(echo $json | jq --arg current_date $(get_current_date) '. + {current_date: $current_date}')
        echo $json_final
        write_to_kafka "$json_final"
done