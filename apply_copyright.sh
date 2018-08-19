#!/usr/bin/env bash

find $(readlink -f $(dirname $0)) -name \*.java | while read f; do
    if [ $f~ -nt $f ]; then
        echo "Skipping $f: backup exists"
    else
        echo "Adding copyright to $f"
        mv -n $f $f~
        {
            cat - $f~ <<EOF
/*
 * This file is part of Clef.
 *
 * Clef is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Clef is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Clef.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
EOF
        } > $f
    fi
done
