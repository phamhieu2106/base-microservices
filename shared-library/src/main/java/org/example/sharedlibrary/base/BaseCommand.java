package org.example.sharedlibrary.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.sharedlibrary.Message;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BaseCommand extends Message {

}
