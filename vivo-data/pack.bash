#!/bin/bash

# This program and the accompanying materials are made available under the
# terms of the MIT license (X11 license) which accompanies this distribution.

# author: C. Bürger

tar -czf - cornell-vivo | split -b 40m - packed-cornell-vivo-
