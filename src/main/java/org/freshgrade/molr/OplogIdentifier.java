package org.freshgrade.molr;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.util.Assert;

/**
 * Created by ronbierman on 15-10-08.
 */
@Getter
public class OplogIdentifier {

    private Integer time;

    private Integer inc;

    public OplogIdentifier(Integer time, Integer inc){
        Assert.notNull(time);
        Assert.notNull(inc);

        this.time = time;
        this.inc = inc;
    }

    public String toString() {
    	LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
        
        return localDateTime + " (" + inc + ")";
    }
}
