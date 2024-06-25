package org.example.sharedlibrary.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.sharedlibrary.Message;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent extends Message {
    private int version;
}
