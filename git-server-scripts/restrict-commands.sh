#!/bin/sh

userId=$1
regex="git-[a-z]+-pack '(.*)'"
apiKey="0987654321"
serverUrlBase="http://localhost:6122"
sshCommand="$SSH_ORIGINAL_COMMAND"

if [[ ${sshCommand} =~ $regex ]]
   then
       repoPath="${BASH_REMATCH[1]}"
   else
       exit 1
fi
echo  "$serverUrlBase/repo/check-user-access?userId=$userId&repoPath=$repoPath"
curl -X GET "$serverUrlBase/repo/check-user-access?userId=$userId&repoPath=$repoPath" -H 'x-api-key: 0987654321'

exec git-shell -c "$sshCommand"