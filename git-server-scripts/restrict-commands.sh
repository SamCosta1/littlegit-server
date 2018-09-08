#!/bin/sh
# commands restricted for ssh-key access

# regex="git-[a-z]+-pack '(.*)'"

#  if [[ "$SSH_ORIGINAL_COMMAND" =~ $regex ]]
#     then
#         repoName="${BASH_REMATCH[1]}"
#     else
#         exit 1
#     fi


exec git-shell -c "$SSH_ORIGINAL_COMMAND"