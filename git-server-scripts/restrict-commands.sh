#!/bin/sh

userId=$1
regex="git-[a-z]+-pack '(.*)'"
apiKey="0987654321"
serverUrlBase="http://localhost:6122"

if [[ "$SSH_ORIGINAL_COMMAND" =~ $regex ]]
   then
       repoPath="${BASH_REMATCH[1]}"
   else
       exit 1
fi

hasAccess=$(curl -s -X GET "$serverUrlBase/repo/check-user-access?userId=$userId&repoPath=$repoPath" -H "x-api-key: $apiKey")
if [[ "$hasAccess" = "true" ]]
    then
        exec git-shell -c "$SSH_ORIGINAL_COMMAND"
    else
        exit 1
fi